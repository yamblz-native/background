package ru.yandex.yamblz.handler;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private Handler handler;
    private Set<Integer> sections;
    private Queue<Task> tasks;
    private Map<Task, Runnable> delayed;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StubCriticalSectionsHandler() {
        handler = new Handler(Looper.getMainLooper());
        sections = new ConcurrentSkipListSet<>();
        tasks = new ConcurrentLinkedDeque<>();
        delayed = new ConcurrentHashMap<>();
    }

    @Override
    public void startSection(int id) {
        Log.w("Handler", "started section " + id);
        sections.add(id);
    }

    @Override
    public void stopSection(int id) {
        sections.remove(id);
        if (sections.size() == 0)
            for (Task task: tasks) {
                tasks.remove(task);
                handler.post(task::run);
            }
    }

    @Override
    public void stopSections() {
        Log.w("Handler", "stopped sections");
        sections.clear();
        for (Task task: tasks) {
            tasks.remove(task);
            handler.post(task::run);
        }
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (sections.size() > 0) {
            tasks.add(task);
        } else {
            tasks.remove(task);
            handler.post(task::run);
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        delay = Math.max(delay, 0);
        if (delay == 0) {
            tasks.remove(task);
            handler.post(task::run);
        } else {
            Runnable delayedTask = () -> postLowPriorityTask(task);
            handler.postDelayed(delayedTask, delay);
            delayed.put(task, delayedTask);
        }
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        Log.w("HANDLER", "REMOVING");
        tasks.remove(task);
        Runnable r = delayed.remove(task);
        if (r != null)
            handler.removeCallbacks(r);
    }

    @Override
    public void removeLowPriorityTasks() {
        for (Task task: tasks)
            removeLowPriorityTask(task);
    }
}
