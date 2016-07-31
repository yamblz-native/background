package ru.yandex.yamblz.handler;

import android.os.Handler;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultCriticalSectionsHandler implements CriticalSectionsHandler {
    private final Handler uiThreadHandler;
    private final ConcurrentLinkedQueue<Task> tasks = new ConcurrentLinkedQueue<>();
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
        sections.clear();
        runTasks();
    }


    @Override
    public void postLowPriorityTask(Task task) {
        if (sections.isEmpty()) {
            runTask(task);
        } else {
            tasks.add(task);
        }
    }


    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        uiThreadHandler.postDelayed(() -> postLowPriorityTask(task), delay);
    }


    @Override
    public void removeLowPriorityTask(Task task) {
        tasks.remove(task);
    }


    @Override
    public void removeLowPriorityTasks() {
        tasks.clear();
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
