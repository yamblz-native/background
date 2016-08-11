package ru.yandex.yamblz.handler;

import android.os.Handler;

import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultCriticalSectionsHandler implements CriticalSectionsHandler {
    private final Handler uiThreadHandler;
    private final Queue<Task> tasks = new ConcurrentLinkedQueue<>();
    private final Queue<Task> featureTasks = new ConcurrentLinkedQueue<>();
    private final Set<Integer> sections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DefaultCriticalSectionsHandler(Handler uiThreadHandler) {
        this.uiThreadHandler = uiThreadHandler;
    }

    @Override
    public void startSection(int id) {
        sections.add(id);
    }

    @Override
    public void stopSection(int id) {
        sections.remove(id);
        if (sections.isEmpty()) {
            runTasks();
        }
    }

    @Override
    public void stopSections() {
        for (Integer section : sections) {
            stopSection(section);
        }
        runTasks();
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (featureTasks.contains(task)) {
            featureTasks.remove(task);
        }
        if (sections.isEmpty()) {
            runTask(task);
        } else {
            tasks.add(task);
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        if (delay <= 0) {
            postLowPriorityTask(task);
        } else {
            featureTasks.add(task);
            uiThreadHandler.postDelayed(() -> postLowPriorityTask(task), delay);
        }
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        tasks.remove(task);
        if (featureTasks.contains(task)) {
            uiThreadHandler.removeCallbacks(() -> postLowPriorityTask(task));
            featureTasks.remove(task);
        }
    }

    @Override
    public void removeLowPriorityTasks() {
        for (Task task : tasks) {
            removeLowPriorityTask(task);
        }
        for (Task task : featureTasks) {
            removeLowPriorityTask(task);
        }
    }

    private void runTasks() {
        for (Task task : tasks) {
            runTask(task);
        }
    }

    private void runTask(Task task) {
        tasks.remove(task);
        uiThreadHandler.post(task::run);
    }
}
