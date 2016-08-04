package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import timber.log.Timber;

public class DefaultCriticalSectionsHandler implements CriticalSectionsHandler {
    private static int MAX_QUEUE_SIZE = 6; // столько влазит на экран ._.
    private ConcurrentLinkedQueue<Task> lowPriorityTasksQueue;
    private CopyOnWriteArrayList<Integer> sections;
    private Handler mainHandler;

    public DefaultCriticalSectionsHandler() {
        lowPriorityTasksQueue = new ConcurrentLinkedQueue<>();
        sections = new CopyOnWriteArrayList<>();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void startSection(int id) {
        Timber.d("Start section with id=" + String.valueOf(id));
        sections.add(id);
    }

    @Override
    public void stopSection(int id) {
        Timber.d("Stop section with id=" + String.valueOf(id));
        sections.remove(sections.indexOf(id));

        if (sections.isEmpty()) {
            runLowPriorityTasksQueue();
        }
    }

    @Override
    public void stopSections() {
        Timber.d("Stop all sections");
        sections.clear();
        runLowPriorityTasksQueue();
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (sections.isEmpty()) {
            Timber.d("Low priority task running");
            mainHandler.post(task::run);
        } else {
            Timber.d("Add to queue");
            lowPriorityTasksQueue.add(task);

            if (lowPriorityTasksQueue.size() > MAX_QUEUE_SIZE) {
                lowPriorityTasksQueue.poll();
            }
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        Timber.d("Post low priority task with delay");
        mainHandler.postDelayed(() -> postLowPriorityTask(task), delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        lowPriorityTasksQueue.remove();
    }

    @Override
    public void removeLowPriorityTasks() {
        lowPriorityTasksQueue.clear();
    }

    private void runLowPriorityTasksQueue() {
        while (!lowPriorityTasksQueue.isEmpty()) {
            Timber.d("Get task from queue");
            Task task = lowPriorityTasksQueue.peek();
            postLowPriorityTask(task);
            removeLowPriorityTask(task);

            if (!sections.isEmpty()) {
                break;
            }
        }
    }
}
