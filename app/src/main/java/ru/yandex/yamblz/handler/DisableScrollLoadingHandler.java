package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.MessageQueue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

/**
 * Created by aleien on 31.07.16.
 */

public class DisableScrollLoadingHandler implements CriticalSectionsHandler {
    private Set<Integer> runningSections = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    List<Task> tasks = new CopyOnWriteArrayList<>();

    @Override
    public void startSection(int id) {
        // Как вести себя в случае если низкоприоритетные таски еще не закончились?
        runningSections.add(id);
    }

    @Override
    public void stopSection(int id) {
        if (runningSections.contains(id)) {
            runningSections.remove(id);
        }
    }

    @Override
    public void stopSections() {
        runningSections.clear();
    }

    synchronized private void runTasks() {
        for (Task task : tasks) {
            task.run();
            removeLowPriorityTask(task);
        }
    }

    @Override
    public void postLowPriorityTask(Task task) {
        tasks.add(task);
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        new Handler().postDelayed(() -> postLowPriorityTask(task), delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        tasks.remove(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        tasks.clear();
    }

    @Override
    public boolean queueIdle() {
        Timber.d("Trying to run tasks, tasks size: %d", tasks.size());
        if (runningSections.size() == 0) runTasks();
        return runningSections.size() == 0 && tasks.size() != 0;
    }
}
