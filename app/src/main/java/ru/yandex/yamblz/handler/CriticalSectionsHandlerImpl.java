package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

public class CriticalSectionsHandlerImpl implements CriticalSectionsHandler {

    private HashSet<Integer> sectionsSet = new HashSet<>();
    private HashSet<Task> tasksInQueue = new HashSet<>();
    private Queue<Task> lowPriorityTasksQueue = new LinkedList<>();

    private Handler mainThreadHandler;

    public CriticalSectionsHandlerImpl(Handler mainThreadHandler) {
        this.mainThreadHandler = mainThreadHandler;
    }

    @Override
    public void startSection(int id) {
        sectionsSet.add(id);
    }

    @Override
    public void stopSection(int id) {
        sectionsSet.remove(id);
        postQueuedTasks();
    }

    @Override
    public void stopSections() {
        sectionsSet.clear();
        postQueuedTasks();
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (isInSection()) {
            lowPriorityTasksQueue.add(task);
            tasksInQueue.add(task);
        }
        else {
            mainThreadHandler.post(task::run);
        }

    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {

    }

    @Override
    public void removeLowPriorityTask(Task task) {

    }

    @Override
    public void removeLowPriorityTasks() {

    }

    boolean isInSection() {
        return sectionsSet.size() > 0;
    }

    void postQueuedTasks() {
        while (!lowPriorityTasksQueue.isEmpty()) {
            Task task = lowPriorityTasksQueue.remove();
            if (tasksInQueue.contains(task)) {
                tasksInQueue.remove(task);
                mainThreadHandler.post(task::run);
            }
        }
    }
}
