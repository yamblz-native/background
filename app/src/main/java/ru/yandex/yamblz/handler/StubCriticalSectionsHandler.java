package ru.yandex.yamblz.handler;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import junit.framework.Test;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private Handler handler = new Handler(Looper.getMainLooper());
    //private LinkedList<Task> taskList = new LinkedList<>();
    private Set<Integer> sections = new HashSet<>();
    private ConcurrentLinkedQueue<Task> taskList = new ConcurrentLinkedQueue<>();
    private Map<Task, Runnable> delayTasks = new LinkedHashMap<>();

    private Runnable callback = new Runnable(){
        @Override
        public void run() {
            Task taskToExecute = null;
            if (taskList.isEmpty()) {
                return;
            }
            taskToExecute = taskList.poll();
            taskToExecute.run();
            if (sections.isEmpty()){
                handler.post(callback);
            }
        }
    };

    @Override
    public void startSection(int id) {
        Log.i("Section", "start " + taskList.size());
        sections.add(id);
        handler.removeCallbacks(callback);
    }

    @Override
    public void stopSection(int id) {
        Log.i("Section", "stop " + taskList.size());
        sections.remove(id);
        if (sections.isEmpty()) {
            handler.post(callback);
        }
    }

    @Override
    public void stopSections() {
        Log.i("Sections", "all stop " + taskList.size());
        sections.clear();
        handler.post(callback);
    }

    @Override
    public void postLowPriorityTask(Task task) {
        Log.i("LowPriotityTask", "start " + taskList.size());
        taskList.add(task);
        if (taskList.size() == 1 && sections.isEmpty()) {
            handler.post(callback);
        }

    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        Log.i("LowPriorityTaskDelay", "start " + taskList.size());
        Runnable runnable = () -> postLowPriorityTask(task);
        delayTasks.put(task, runnable);
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        Log.i("RemoveLwPriotityTask", "start " + taskList.size());
        taskList.remove(task);
        handler.removeCallbacks(delayTasks.get(task));
    }

    @Override
    public void removeLowPriorityTasks() {
        taskList.clear();
    }


}
