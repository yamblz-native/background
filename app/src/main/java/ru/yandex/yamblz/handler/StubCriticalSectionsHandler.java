package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {
    private static final String TAG = "StubCritSectHandler";
    private final MyHandler handler;
    private SparseArray<Object> sparseArray;

    public StubCriticalSectionsHandler() {
        sparseArray = new SparseArray<>();
        handler = new MyHandler(Looper.getMainLooper());
    }

    @Override
    public void startSection(int id) {
        sparseArray.put(id, null);
        checkSections();
    }

    @Override
    public void stopSection(int id) {
        sparseArray.delete(id);
        checkSections();
    }

    @Override
    public void stopSections() {
        sparseArray.clear();
        checkSections();
    }


    private void checkSections() {
        if (sparseArray.size() == 0) {
            Log.d(TAG, "start handler");
            handler.resume();
        } else {
            Log.d(TAG, "stop handler");
            handler.pause();
        }
    }

    @Override
    public void postLowPriorityTask(Task task) {
        postLowPriorityTaskDelayed(task, 0);
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        handler.postDelayedTask(task, delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        handler.removeTask(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        handler.removeTasks();
    }

    private class MyHandler extends Handler {
        private List<TaskRunnable> allRunnables;
        private List<TaskRunnable> postRunnables;
        private boolean isRunning = true;

        MyHandler(Looper looper) {
            super(looper);
            postRunnables = new ArrayList<>();
            allRunnables = new ArrayList<>();
        }


        @Override
        public void dispatchMessage(Message msg) {
            if (!isRunning) {
                if (msg.getCallback() == null) {;
                    super.dispatchMessage(msg);
                } else {
                    postRunnables.add((TaskRunnable) msg.getCallback());
                }
            } else {
                removeTask(((TaskRunnable) msg.getCallback()).task);
                super.dispatchMessage(msg);
            }
        }

        void pause() {
            isRunning = false;
        }

        void resume() {
            if (isRunning == true) return;
            isRunning = true;
            for (Runnable m : postRunnables) {
                m.run();
            }
            postRunnables.clear();
        }

        //todo нужно оптимизировать иначе если таск много будут проблемы
        void removeTask(Task task) {
            Iterator<TaskRunnable> iterator= postRunnables.iterator();
            while (iterator.hasNext()){
                TaskRunnable taskRunnable=iterator.next();
                if(taskRunnable.task==task){
                    iterator.remove();
                    Log.d("MyHandler","task removed:"+taskRunnable);
                }
            }
            iterator= allRunnables.iterator();
            while (iterator.hasNext()){
                TaskRunnable taskRunnable=iterator.next();
                if(taskRunnable.task==task){
                    removeCallbacks(taskRunnable);
                    iterator.remove();
                    Log.d("MyHandler","task removed:"+taskRunnable);
                }
            }
        }

        void removeTasks() {
            postRunnables.clear();
            for(TaskRunnable t:allRunnables){
                removeCallbacks(t);
            }
        }

        void postDelayedTask(Task task,long millis){
            TaskRunnable taskRunnable=new TaskRunnable(task);
            allRunnables.add(taskRunnable);
            super.postDelayed(taskRunnable,millis);
        }
    }

    private static class TaskRunnable implements Runnable {
        Task task;

        TaskRunnable(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            task.run();
        }

    }

}
