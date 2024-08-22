import jdk.nashorn.internal.runtime.ParserException;
import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Ralitsa Dimitrova
 */
public class Main {
    public static void main (String[] args) throws IOException {
        MbrotContext context = processCmdLineArguments(args);
        if (context.helpMode) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ralitsa-mandelbrot", Constants.programOptions);
            return;
        }
        runMandelbrotTest(context);
    }

    public static MbrotContext processCmdLineArguments(String[] args) {
        MbrotContext.MbrotContextBuilder contextBuilder = new MbrotContext.MbrotContextBuilder();
        CommandLineParser parser = new DefaultParser(false);
        CommandLine cmd;

        try {
            cmd = parser.parse(Constants.programOptions, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return contextBuilder.build();
        }

        if (cmd.hasOption(Constants.HELP_OPT)) {
            contextBuilder.helpMode(true);
            return contextBuilder.build();
        }


        if (cmd.hasOption(Constants.COMPARE_THREADS_OPT)) {
            contextBuilder.compareThreadsMode(true);
        } else if (cmd.hasOption(Constants.QUIET_OPT)) {
            contextBuilder.quietMode(true);
        }

        if (cmd.hasOption("s")) {
            String[] sizeOptionValue = cmd.getOptionValues("s");
            if (sizeOptionValue.length != 2) {
                throw new ParserException("");
            }
            contextBuilder.width(Integer.parseInt(sizeOptionValue[0]));
            contextBuilder.height(Integer.parseInt(sizeOptionValue[1]));
        }

        if (cmd.hasOption("r")) {
            String[] sizeOptionValue = cmd.getOptionValues("r");
            if (sizeOptionValue.length != 4) {
                throw new ParserException("");
            }
            contextBuilder.aStart(Double.parseDouble(sizeOptionValue[0]));
            contextBuilder.aEnd(Double.parseDouble(sizeOptionValue[1]));
            contextBuilder.bStart(Double.parseDouble(sizeOptionValue[2]));
            contextBuilder.bEnd(Double.parseDouble(sizeOptionValue[3]));
        }

        if (cmd.hasOption("o")) {
            contextBuilder.outputFileNamePrefix(cmd.getOptionValue("o"));
        }

        if (cmd.hasOption("t")) {
            contextBuilder.threadCount(Integer.parseInt(cmd.getOptionValue("t")));
        }

        if (cmd.hasOption("g")) {
            contextBuilder.granularity(Integer.parseInt(cmd.getOptionValue("g")));
        }

        if (cmd.hasOption("i")) {
            int maxIterations = Integer.parseInt(cmd.getOptionValue("i"));
            if (maxIterations < 1) {
                throw new ParserException("");
            }
            contextBuilder.maxIterations(Integer.parseInt(cmd.getOptionValue("i")));
        }

        if (cmd.hasOption("b")) {
            contextBuilder.loadBalancing(Enum.valueOf(Constants.LoadBalancing.class, cmd.getOptionValue("b").toUpperCase()));
        }

        if (cmd.hasOption("m")) {
            contextBuilder.matrixDivision(Enum.valueOf(Constants.MatrixDivision.class, cmd.getOptionValue("m").toUpperCase()));
        }

        return contextBuilder.build();
    }

    public static void runMandelbrotTest(MbrotContext context) throws IOException {
        long startTime = System.currentTimeMillis();

        BufferedImage bi = new BufferedImage(context.width, context.height, BufferedImage.TYPE_INT_RGB);

        List<MbrotTask> tasks = generateTasks(context);
        List<MbrotTask> resultList = Collections.synchronizedList(new ArrayList<>());

        if (context.loadBalancing.equals(Constants.LoadBalancing.DYNAMIC)) {
            BlockingQueue<MbrotTask> taskQueue = new LinkedBlockingQueue<>(tasks);

            Thread[] threads = new Thread[context.threadCount - 1];
            for (int i = 0; i < context.threadCount - 1; i++) {
                threads[i] = new Thread(new DynamicWorker(i, taskQueue, resultList, context));
                threads[i].start();
            }

            for (Thread worker : threads) {
                try {
                    worker.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        } else {
            List<List<MbrotTask>> taskDistribution = distributeTasks(context, tasks);

            Thread[] threads = new Thread[context.threadCount - 1];
            for (int i = 0; i < context.threadCount - 1; i++) {
                threads[i] = new Thread(new StaticWorker(i+1, taskDistribution.get(i+1), resultList, context));
                threads[i].start();
            }

            new StaticWorker(0, taskDistribution.get(0), resultList, context).run();

            for (Thread worker : threads) {
                try {
                    worker.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        for (MbrotTask result : resultList) {
            bi.setRGB(result.startX,
                    result.startY,
                    result.width,
                    result.height,
                    result.rgbArray,
                    0,
                    result.width);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (!context.compareThreadsMode) {
            if (!context.quietMode) {
                System.out.println(String.format("Total execution time for current run (millis): %d", duration));
            } else {
                System.out.println(duration);
            }
        }

        String runId = UUID.randomUUID().toString();
        String fileName = String.format("rendered-images/%s_%s_%s", context.outputFileNamePrefix, new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new java.util.Date()), runId);
        File imageFile = new File(fileName + "_image.png");
        Files.createDirectories(Paths.get("rendered-images"));
        ImageIO.write(bi, "png", imageFile);

        File optionsTextFile = new File(fileName + "_options.txt");
        FileWriter myWriter = new FileWriter(optionsTextFile + ".txt");
        myWriter.write(context.printContext());
        myWriter.close();
    }

    private static List<MbrotTask> generateTasks(MbrotContext context) {
        List<MbrotTask> tasks = new ArrayList<>();

        int subtaskCount = context.granularity * context.threadCount;

        int numRows;
        int numCols;

        switch (context.matrixDivision) {
            case ROWS:
                numRows = subtaskCount;
                numCols = 1;
                break;
            case COLUMNS:
                numRows = 1;
                numCols = subtaskCount;
                break;
            case MATRICES:
                numRows = (int) Math.sqrt(subtaskCount);
                numCols = (int) Math.ceil((double) subtaskCount / numRows);
                break;
            default:
                throw new IllegalArgumentException("Invalid matrix division strategy");
        }

        int baseWidth = context.width / numCols;
        int baseHeight = context.height / numRows;
        int widthRemainder = context.width % numCols;
        int heightRemainder = context.height % numRows;

        int startX = 0;
        for (int col = 0; col < numCols; col++) {
            int currentWidth = baseWidth + (col < widthRemainder ? 1 : 0);
            int startY = 0;
            for (int row = 0; row < numRows; row++) {
                int currentHeight = baseHeight + (row < heightRemainder ? 1 : 0);
                tasks.add(new MbrotTask(col + row, startX, startY, currentWidth, currentHeight, context));
                startY += currentHeight;
            }
            startX += currentWidth;
        }

        return tasks;
    }

    private static List<List<MbrotTask>> distributeTasks(MbrotContext context, List<MbrotTask> tasks) {
        List<List<MbrotTask>> taskDistribution = new ArrayList<>();

        for (int i = 0; i < context.threadCount; i++) {
            taskDistribution.add(new ArrayList<>());
        }

        // Distribute the shuffled tasks to the threads
        for (int i = 0; i < tasks.size(); i++) {

            taskDistribution.get(i % context.threadCount).add(tasks.get(i));
        }

        return taskDistribution;
    }
}
