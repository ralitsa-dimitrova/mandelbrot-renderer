import java.util.List;

public class StaticWorker implements Runnable {
    private final int threadId;
    private final List<MbrotTask> tasks;
    private final List<MbrotTask> resultList;
    private final MbrotContext context;

    public StaticWorker(int threadId, List<MbrotTask> tasks, List<MbrotTask> resultList, MbrotContext context) {
        this.threadId = threadId;
        this.tasks = tasks;
        this.resultList = resultList;
        this.context = context;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        for (MbrotTask task : tasks) {
            task.execute();
            synchronized (resultList) {
                resultList.add(task);
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (context.compareThreadsMode) {
            System.out.println(String.format("%d,%d,%d", this.threadId, duration, tasks.size()));
        } else if (!context.quietMode) {
            System.out.println(String.format("Thread-%d stopped. Execution time was (millis): %d (%d tasks)", this.threadId, duration, tasks.size()));
        }
    }
}
