package ru.yandex.yamblz.handler;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private final ScheduledThreadPoolExecutor delayedExecutor = new ScheduledThreadPoolExecutor(5);
    private Queue<Task> lowPriorityQueue;
    private List<Integer> criticalSections;
    private UIThreadExecutor uiThreadExecutor;

    public StubCriticalSectionsHandler(@NonNull UIThreadExecutor uiThreadExecutor) {
        this.uiThreadExecutor = uiThreadExecutor;
        lowPriorityQueue = new ConcurrentLinkedQueue<>();
        criticalSections = new CopyOnWriteArrayList<>();
    }

    @Override
    public void startSection(int id) {
        if(!criticalSections.contains(id)){
            criticalSections.add(id);
        }
    }

    @Override
    public void stopSection(int id) {
        criticalSections.remove((Object)id);
        runNextTask();
    }

    @Override
    public void stopSections() {
        criticalSections.clear();
        runNextTask();
    }

    @Override
    public void postLowPriorityTask(Task task) {
        lowPriorityQueue.add(task);
        runNextTask();
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        delayedExecutor.schedule(() -> postLowPriorityTask(task), delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        if (lowPriorityQueue.contains(task)) {
            lowPriorityQueue.remove(task);
        }
    }

    @Override
    public void removeLowPriorityTasks() {
        lowPriorityQueue.clear();
    }

    public int lowPriorityTasksCount() {
        return lowPriorityQueue.size();
    }

    public int criticalSectionsCount() {
        return criticalSections.size();
    }

    private synchronized void runNextTask() {
        if (criticalSections.size() == 0 && lowPriorityQueue.size() > 0) {
            Task nextTask = lowPriorityQueue.element();
            uiThreadExecutor.postOnUIThread(nextTask);
            lowPriorityQueue.remove(nextTask);
            if (lowPriorityQueue.size() > 0) {
                runNextTask();
            }
        }
    }
}
