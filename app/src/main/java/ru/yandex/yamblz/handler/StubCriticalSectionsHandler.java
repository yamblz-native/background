package ru.yandex.yamblz.handler;


import android.os.Handler;
import android.os.Looper;

import junit.framework.Test;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private Handler handler = new Handler(Looper.getMainLooper());
    private LinkedList<Task> taskList = new LinkedList<>();
    private Set<Integer> sections = new LinkedHashSet<>();
    private Map<Task, Runnable> delayTasks = new LinkedHashMap<>();

    private Runnable callback = new Runnable(){
        @Override
        public void run() {
            if (taskList.isEmpty()) {
                return;
            }
            Task taskToExecute = taskList.pop();
            taskToExecute.run();
            if (sections.isEmpty()){
                handler.post(callback);
            }
        }
    };

    @Override
    public void startSection(int id) {
        sections.add(id);
        handler.removeCallbacks(callback);
    }

    @Override
    public void stopSection(int id) {
        sections.remove(id);
        if (sections.isEmpty()) {
            handler.post(callback);
        }
    }

    @Override
    public void stopSections() {
        sections.clear();
        handler.post(callback);
    }

    @Override
    public void postLowPriorityTask(Task task) {
        taskList.add(task);
        if (taskList.size() == 1 && sections.isEmpty()) {
            handler.post(callback);
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                postLowPriorityTask(task);
            }
        };
        delayTasks.put(task, runnable);
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        taskList.remove(task);
        handler.removeCallbacks(delayTasks.get(task));
    }

    @Override
    public void removeLowPriorityTasks() {
        taskList.clear();
    }


}
