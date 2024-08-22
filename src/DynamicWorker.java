import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * @author Ralitsa Dimitrova
 */
public class DynamicWorker implements Runnable {
    private final MbrotContext context;
    private final int threadId;
    private final BlockingQueue<MbrotTask> taskQueue;
    private final List<MbrotTask> resultList;
    private int taskCount = 0;

    public DynamicWorker(int threadId, BlockingQueue<MbrotTask> taskQueue, List<MbrotTask> resultList, MbrotContext context) {
        this.threadId = threadId;
        this.taskQueue = taskQueue;
        this.resultList = resultList;
        this.context = context;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                if (taskQueue.isEmpty()) {
                    break;
                }

                // Take a task from the queue and execute it
                MbrotTask task = taskQueue.take();
                task.execute();
                taskCount++;

                // Add the result to the result list in a thread-safe manner
                synchronized (resultList) {
                    resultList.add(task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (context.compareThreadsMode) {
            System.out.println(String.format("%d,%d,%d", this.threadId, duration, taskCount));
        } else if (!context.quietMode) {
            System.out.println(String.format("Thread-%d stopped. Execution time was (millis): %d (%d tasks)", this.threadId, duration, taskCount));
        }
    }
}