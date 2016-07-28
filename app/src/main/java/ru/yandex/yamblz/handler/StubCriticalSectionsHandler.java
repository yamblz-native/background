package ru.yandex.yamblz.handler;

import android.os.Handler;

import java.util.concurrent.CopyOnWriteArrayList;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private final Handler handler;
    private final CopyOnWriteArrayList<Integer> criticalSections = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Task> lowPriorityTasks = new CopyOnWriteArrayList<>();
    private final LowPriorityTaskExecutor executor = new LowPriorityTaskExecutor();

    public StubCriticalSectionsHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void startSection(int id) {
        if (!criticalSections.contains(id)) {
            criticalSections.add(id);
            handler.removeCallbacks(executor);
        }
    }

    @Override
    public void stopSection(int id) {
        criticalSections.remove(Integer.valueOf(id));
        if (criticalSections.isEmpty()) {
            handler.post(executor);
        }
    }

    @Override
    public void stopSections() {
        criticalSections.clear();
        handler.post(executor);
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (criticalSections.isEmpty() && lowPriorityTasks.isEmpty()) {
            lowPriorityTasks.add(task);
            handler.post(executor);
        } else {
            lowPriorityTasks.add(task);
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        lowPriorityTasks.add(task);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        lowPriorityTasks.remove(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        handler.removeCallbacks(executor);
        lowPriorityTasks.clear();
    }


    public class LowPriorityTaskExecutor implements Runnable {
        @Override
        public void run() {
            executeLowPriorityTask();
        }

        public void executeLowPriorityTask() {
            Task currentTask;
            while (criticalSections.isEmpty() && !lowPriorityTasks.isEmpty()) {
                currentTask = lowPriorityTasks.remove(0);
                currentTask.run();
            }
        }
    }

}
