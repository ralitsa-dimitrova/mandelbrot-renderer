import org.apache.commons.cli.*;

public class Constants {
    public static final String QUIET_OPT = "q";
    public static final String COMPARE_THREADS_OPT = "c";
    public static final String HELP_OPT = "h";


    public static final Option sizeOpt = Option.builder()
            .option("s")
            .longOpt("size")
            .desc("The size of the generated image. Supplied as a string in a \"{width}x{height}\" format.")
            .hasArgs()
            .argName("imageSize")
            .numberOfArgs(2)
            .valueSeparator('x')
            .type(Integer.class)
            .build();

    public static final Option rectOpt = Option.builder()
            .option("r")
            .longOpt("rect")
            .desc("Area of the complex plane to compute. Must be provided in the format \"aStart:aEnd:bStart:bEnd\", where (aStart,bStart) is the top left coordinate and (aEnd, bEnd) is the bottom right coordinate of the area.")
            .hasArgs()
            .argName("complexPlaneArea")
            .numberOfArgs(4)
            .valueSeparator(':')
            .type(Double.class)
            .build();

    public static final Option threadsOpt = Option.builder()
            .option("t")
            .longOpt("threads")
            .desc("Number of threads")
            .hasArg()
            .argName("threadCount")
            .type(Integer.class)
            .build();

    public static final Option granularityOpt = Option.builder()
            .option("g")
            .longOpt("granularity")
            .desc("The granularity of the program. Total task count = number of threads * granularity")
            .hasArg()
            .argName("granularity")
            .type(Integer.class)
            .build();

    public static final Option outputOpt = Option.builder()
            .option("o")
            .longOpt("output")
            .desc("Prefix used for the name of the output files. Two files are generated - an image and a text file with the options used to generate it. Their names are respectively \"prefix_date_uuid_image.png\" and \"prefix_date_uuid_options.txt\"")
            .hasArg()
            .argName("outputFileNamePrefix")
            .type(String.class)
            .build();

    public static final Option iterationsOpt = Option.builder()
            .option("i")
            .longOpt("iterations")
            .desc("Max number of  iterations to determine of a point belongs to the Mandelbrot set")
            .hasArg()
            .argName("maxIterations")
            .type(Integer.class)
            .build();

    public static final Option loadBalancingOpt = Option.builder()
            .option("b")
            .longOpt("loadBalancing")
            .desc("Load balancing strategy. One of the following values: [\"dynamic\", \"static\"]")
            .hasArg()
            .argName("loadBalancingType")
            .type(String.class)
            .build();

    public static final Option matrixDivisionOpt = Option.builder()
            .option("m")
            .longOpt("matrixDivision")
            .desc("Matrix division strategy. One of the following values: [\"rows\", \"columns\", \"matrices\"]. Divides the image into {granularity * threadCount} parts of the provided type.")
            .hasArg()
            .argName("matrixDivisionType")
            .type(String.class)
            .build();

    public static final Option quietOpt = Option.builder()
            .option("q")
            .longOpt("quiet")
            .desc("When this flag is enabled, the program outputs a single number - the total duration of the program execution. ")
            .build();

    public static final Option compareThreadsOpt = Option.builder()
            .option("c")
            .longOpt("compareThreads")
            .desc("When this flag is enabled, the program outputs {threadCount} lines in the format \"threadId,executionTime,taskCount\". Note that the lines will not be sorted by threadId, rather by execution end time of the corresponding thread. Collection must be sorted additionally by the user before inspecting the results. If -q flag is also provided, this flag will override it, and the total program execution time will not be printed.")
            .build();

    public static final Option helpOpt = Option.builder("h")
            .option("h")
            .longOpt("help")
            .desc("Displays instructions for use of the command line arguments")
            .build();

    public static final Options programOptions = new Options()
            .addOption(Constants.sizeOpt)
            .addOption(Constants.rectOpt)
            .addOption(Constants.threadsOpt)
            .addOption(Constants.granularityOpt)
            .addOption(Constants.outputOpt)
            .addOption(Constants.iterationsOpt)
            .addOption(Constants.loadBalancingOpt)
            .addOption(Constants.matrixDivisionOpt)
            .addOption(Constants.quietOpt)
            .addOption(Constants.compareThreadsOpt)
            .addOption(Constants.helpOpt);

    public static final double ESCAPE_RADIUS = 2.0;

    public enum LoadBalancing {
        STATIC,
        DYNAMIC
    }

    public enum MatrixDivision {
        ROWS,
        COLUMNS,
        MATRICES
    }
}
