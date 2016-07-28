package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private Set<Integer> sections = new HashSet<>();
    private LinkedList<Task> taskQueue = new LinkedList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private HashMap<Task, Runnable> delayedTask = new HashMap<>();
    private Runnable callBack = new Runnable() {
        @Override
        public void run() {
            if (taskQueue.isEmpty()) {
                return;
            }

            Task firstTask = taskQueue.pop();
            firstTask.run();

            if (sections.isEmpty()) {
                handler.post(callBack);
            }
        }
    };

    @Override
    public void startSection(int id) {
        Log.d("startSection", Integer.toString(sections.size()));
        sections.add(id);
        Log.d("startSection", Integer.toString(sections.size()));
        handler.removeCallbacks(callBack);
    }

    @Override
    public void stopSection(int id) {
        Log.d("stopSection", Integer.toString(sections.size()));
        sections.remove(id);
        Log.d("stopSection", Integer.toString(sections.size()));
        if (sections.isEmpty()) {
            handler.post(callBack);
        }
    }

    @Override
    public void stopSections() {
        sections.clear();
        handler.post(callBack);
    }

    @Override
    public void postLowPriorityTask(Task task) {
        Log.d("postLowPriorityTask", "post");
        taskQueue.add(task);
        if (taskQueue.size() == 1 && sections.isEmpty()) {
            handler.post(callBack);
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        Runnable runnable = () -> postLowPriorityTask(task);
        delayedTask.put(task, runnable);
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        taskQueue.remove(task);
        handler.removeCallbacks(delayedTask.get(task));
    }

    @Override
    public void removeLowPriorityTasks() {
        taskQueue.clear();
    }
}
