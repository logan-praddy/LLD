package TaskSchedular;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

enum ScheduledTaskType {
    RUN_ONCE, RECUR, RECUR_WITH_DELAY;
}

class ScheduledTask {
    private Runnable task;
    private Long scheduledTime;
    private ScheduledTaskType scheduledTaskType;
    private Long delay;
    private Long replayTime;
    private TimeUnit timeUnit;

    ScheduledTask(Runnable task, Long scheduledTime, ScheduledTaskType scheduledTaskType, Long delay, Long replayTime, TimeUnit timeUnit) {
        this.task = task;
        this.scheduledTime = scheduledTime;
        this.scheduledTaskType = scheduledTaskType;
        this.delay = delay;
        this.replayTime = replayTime;
        this.timeUnit = timeUnit;
    }

    public Runnable getTask() {
        return task;
    }

    public Long getScheduledTime() {
        return scheduledTime;
    }

    public ScheduledTaskType getScheduledTaskType() {
        return scheduledTaskType;
    }

    public Long getDelay() {
        return delay;
    }

    public Long getReplayTime() {
        return replayTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}

interface ISchedulerService extends Runnable {
    void scheduleTask(Runnable task, Long delay, TimeUnit timeUnit);
    void scheduleRecurringTask(Runnable task, Long delay, Long replayTime, TimeUnit timeUnit);
    void scheduleRecurringTaskWithWait(Runnable task, Long delay, Long replayTime, TimeUnit timeUnit);
}

class Scheduler implements ISchedulerService {
    private static ISchedulerService schedulerService;
    private final PriorityQueue<ScheduledTask> taskPriorityQueue;
    private final ThreadPoolExecutor taskExecutor;
    private final Lock lock;
    private final Condition newTaskScheduled;

    private Scheduler() {
        this.taskPriorityQueue = new PriorityQueue<>(Comparator.comparingLong(ScheduledTask::getScheduledTime));
        this.taskExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.lock = new ReentrantLock();
        this.newTaskScheduled = this.lock.newCondition();
    }

    public static synchronized ISchedulerService getInstance() {
        if (schedulerService == null) {
            schedulerService = new Scheduler();
        }
        return schedulerService;
    }

    @Override
    public void run() {
        Long sleepFor;
        while (true) {
            this.lock.lock();
            try {
                while (taskPriorityQueue.isEmpty()) {
                    newTaskScheduled.await();
                }

                while (!taskPriorityQueue.isEmpty()) {
                    sleepFor = this.taskPriorityQueue.peek().getTimeUnit().toMillis(this.taskPriorityQueue.peek().getScheduledTime()) - System.currentTimeMillis();
                    if (sleepFor <= 0) {
                        break;
                    }
                    this.newTaskScheduled.await(sleepFor, TimeUnit.MILLISECONDS);
                }

                ScheduledTask scheduledTask = this.taskPriorityQueue.poll();
                Long newScheduledTime;

                switch (scheduledTask.getScheduledTaskType()) {
                    case RUN_ONCE:
                        this.taskExecutor.submit(scheduledTask.getTask());
                        break;
                    case RECUR:
                        newScheduledTime = System.currentTimeMillis() + scheduledTask.getTimeUnit().toMillis(scheduledTask.getReplayTime());
                        this.taskExecutor.submit(scheduledTask.getTask());
                        scheduledTask = new ScheduledTask(scheduledTask.getTask(), newScheduledTime, scheduledTask.getScheduledTaskType(), scheduledTask.getDelay(), scheduledTask.getReplayTime(), scheduledTask.getTimeUnit());
                        this.taskPriorityQueue.add(scheduledTask);
                        break;
                    case RECUR_WITH_DELAY:
                        Future<?> future = this.taskExecutor.submit(scheduledTask.getTask());
                        future.get();
                        newScheduledTime = System.currentTimeMillis() + scheduledTask.getTimeUnit().toMillis(scheduledTask.getReplayTime());
                        scheduledTask = new ScheduledTask(scheduledTask.getTask(), newScheduledTime, scheduledTask.getScheduledTaskType(), scheduledTask.getDelay(), scheduledTask.getReplayTime(), scheduledTask.getTimeUnit());
                        this.taskPriorityQueue.add(scheduledTask);
                        break;
                }
            } catch (RejectedExecutionException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                this.lock.unlock();
            }
        }
    }

    @Override
    public void scheduleTask(Runnable task, Long delay, TimeUnit timeUnit) {
        this.lock.lock();
        try {
            Long scheduledTime = System.currentTimeMillis() + timeUnit.toMillis(delay);
            ScheduledTask scheduledTask = new ScheduledTask(task, scheduledTime, ScheduledTaskType.RUN_ONCE, delay, null, timeUnit);
            taskPriorityQueue.add(scheduledTask);
            newTaskScheduled.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void scheduleRecurringTask(Runnable task, Long delay, Long replayTime, TimeUnit timeUnit) {
        this.lock.lock();
        try {
            Long scheduledTime = System.currentTimeMillis() + timeUnit.toMillis(delay);
            ScheduledTask scheduledTask = new ScheduledTask(task, scheduledTime, ScheduledTaskType.RECUR, delay, replayTime, timeUnit);
            taskPriorityQueue.add(scheduledTask);
            newTaskScheduled.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void scheduleRecurringTaskWithWait(Runnable task, Long delay, Long replayTime, TimeUnit timeUnit) {
        this.lock.lock();
        try {
            Long scheduledTime = System.currentTimeMillis() + timeUnit.toMillis(delay);
            ScheduledTask scheduledTask = new ScheduledTask(task, scheduledTime, ScheduledTaskType.RECUR_WITH_DELAY, delay, replayTime, timeUnit);
            taskPriorityQueue.add(scheduledTask);
            newTaskScheduled.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

//    public void shutdown() {
//        taskExecutor.shutdown();
//    }
//
//    public void shutdownNow() {
//        taskExecutor.shutdownNow();
//    }
}

class TaskCreator {
    public static Runnable createRunnableTask(String taskName) {
        return () -> {
            System.out.printf("Starting %s at %d%n", taskName, System.currentTimeMillis());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Ending %s at %d%n", taskName, System.currentTimeMillis());
        };
    }
}

public class SchedulerExecutor {
    public static void main(String[] args) {
        ISchedulerService schedulerService = Scheduler.getInstance();
        Runnable task1 = TaskCreator.createRunnableTask("Task1");
        Runnable task2 = TaskCreator.createRunnableTask("Task2");
        Runnable task3 = TaskCreator.createRunnableTask("Task3");
        Runnable task4 = TaskCreator.createRunnableTask("Task4");

        schedulerService.scheduleTask(task1, 1L, TimeUnit.MILLISECONDS);
        schedulerService.scheduleTask(task2, 5L, TimeUnit.MILLISECONDS);
        schedulerService.scheduleRecurringTask(task3, 5L, 5L, TimeUnit.MILLISECONDS);
        schedulerService.scheduleRecurringTaskWithWait(task4, 5L, 1L, TimeUnit.MILLISECONDS);

        new Thread((Runnable) schedulerService).start();
    }
}
