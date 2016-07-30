package ru.yandex.yamblz.handler;

import android.util.Log;

public class DefaultCriticalSectionsHandler implements CriticalSectionsHandler {
    private static final String TAG = DefaultCriticalSectionsHandler.class.getSimpleName();

    @Override
    public void startSection(int id) {
        Log.d(TAG, "startSection() " + id);
    }


    @Override
    public void stopSection(int id) {
        Log.d(TAG, "stopSection() " + id);
    }


    @Override
    public void stopSections() {
        Log.d(TAG, "stopSections()");
    }


    @Override
    public void postLowPriorityTask(Task task) {
        Log.d(TAG, "postLowPriorityTask() " + task);
    }


    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        Log.d(TAG, "postLowPriorityTaskDelayed() [" + delay + "] " + task);
    }


    @Override
    public void removeLowPriorityTask(Task task) {
        Log.d(TAG, "removeLowPriorityTask() " + task);
    }


    @Override
    public void removeLowPriorityTasks() {
        Log.d(TAG, "removeLowPriorityTasks()");
    }
}
