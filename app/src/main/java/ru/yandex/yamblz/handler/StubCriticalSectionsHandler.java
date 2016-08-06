package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.Looper;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import timber.log.Timber;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private Handler mainHandler;
    private Queue<Task> taskQueue;
    private Set<Integer> criticalSections;

    StubCriticalSectionsHandler() {
        mainHandler = new Handler(Looper.getMainLooper());
        taskQueue = new ConcurrentLinkedQueue<>();
        criticalSections = new HashSet<>();
    }

    @Override
    public void startSection(int id) {
        criticalSections.add(id);
    }

    @Override
    public void stopSection(int id) {
        criticalSections.remove(id);
        if (criticalSections.isEmpty()) {
            startLowPriorityTask();
        }
    }

    @Override
    public void stopSections() {
        criticalSections.clear();
        startLowPriorityTask();
    }

    @Override
    public Task postLowPriorityTask(Task task) {
        taskQueue.add(task);
        startLowPriorityTask();
        return task;
    }

    @Override
    public Task postLowPriorityTaskDelayed(Task task, int delay) {
        mainHandler.postDelayed(() -> postLowPriorityTask(task), delay);
        return task;
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        taskQueue.remove(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        taskQueue.clear();
    }

    private void startLowPriorityTask() {
        Timber.d("low priority size = " + taskQueue.size());
        while (!taskQueue.isEmpty() && criticalSections.isEmpty()) {
            Task task = taskQueue.poll();
            Timber.d("start task");
            mainHandler.post(() -> task.run());
        }
    }
}
