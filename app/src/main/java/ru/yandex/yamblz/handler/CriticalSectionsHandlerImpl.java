package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Non thread-safe implementation of {@link CriticalSectionsHandler}.
 */
@SuppressWarnings("WeakerAccess")
public class CriticalSectionsHandlerImpl implements CriticalSectionsHandler {

    private final Handler handler;
    private final Set<Integer> sections;

    private final Runnable executeTask;
    private final Set<Task> taskQueue;

    private final Map<Task, Runnable> delayedTasks;


    public CriticalSectionsHandlerImpl() {
        handler = new Handler(Looper.getMainLooper());
        sections = new HashSet<>();

        executeTask = buildExecuteTask();
        taskQueue = new LinkedHashSet<>();

        delayedTasks = new HashMap<>();
    }

    private Runnable buildExecuteTask() {
        return () -> {
            Task currentTask = null;
            currentTask = taskQueue.iterator().next();
            taskQueue.remove(currentTask);

            // This runnable is in execution queue iff taskQueue is not empty.
            assert currentTask != null;
            currentTask.run();

            if (!taskQueue.isEmpty() && sections.isEmpty()) {
                handler.post(executeTask);
            }
        };
    }

    @Override
    public void startSection(int id) {
        sections.add(id);
        if (!taskQueue.isEmpty()) {
            handler.removeCallbacks(executeTask);
        }
    }

    @Override
    public void stopSection(int id) {
        sections.remove(id);
        if (sections.isEmpty()) {
            if (!taskQueue.isEmpty()) {
                handler.post(executeTask);
            }
        }
    }

    @Override
    public void stopSections() {
        sections.clear();
        if (!taskQueue.isEmpty()) {
            handler.post(executeTask);
        }
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (taskQueue.add(task) && sections.isEmpty() && taskQueue.size() == 1) {
            handler.post(executeTask);
        }
        delayedTasks.remove(task);
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delayMillis) {
        if (taskQueue.contains(task)) {
            return;
        }

        Runnable runnable = () -> {
            if (delayedTasks.containsKey(task)) {
                postLowPriorityTask(task);
            }
        };
        delayedTasks.put(task, runnable);
        handler.postDelayed(runnable, delayMillis);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        if (taskQueue.contains(task)) {
            taskQueue.remove(task);
            if (taskQueue.isEmpty()) {
                handler.removeCallbacks(executeTask);
            }
        }
        if (delayedTasks.containsKey(task)) {
            delayedTasks.remove(task);
        }
    }

    @Override
    public void removeLowPriorityTasks() {
        taskQueue.clear();
        delayedTasks.clear();
        handler.removeCallbacks(executeTask);
    }
}
