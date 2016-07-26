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
    private final MyHandler mainHandler;
    private SparseArray<Object> sparseArray;

    public StubCriticalSectionsHandler() {
        sparseArray = new SparseArray<>();
        mainHandler = new MyHandler(Looper.getMainLooper());
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
            Log.d(TAG, "start mainHandler");
            mainHandler.resume();
        } else {
            Log.d(TAG, "stop mainHandler");
            mainHandler.pause();
        }
    }

    @Override
    public void postLowPriorityTask(Task task) {
        mainHandler.postDelayedTask(task,0);
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        mainHandler.postDelayedTask(task,delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        mainHandler.removeTask(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        mainHandler.removeTasks();
    }

    /*
    Хендлеры зранят ссылки на TaskRunnable что-бы была возможность отменить таски
     */

    private static class MyHandler extends Handler {
        private final DelayedHandler delayedHandler;
        private List<TaskRunnable> postRunnables;
        private boolean isRunning = true;

        MyHandler(Looper looper) {
            super(looper);
            delayedHandler=new DelayedHandler(getLooper());
            postRunnables = new ArrayList<>();
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

        void removeTask(Task task) {
            //удаляем таску если она есть в паузе
            Iterator<TaskRunnable> iterator= postRunnables.iterator();
            while (iterator.hasNext()){
                TaskRunnable taskRunnable=iterator.next();
                if(taskRunnable.task==task){
                    iterator.remove();
                    Log.d("MyHandler","task removed:"+taskRunnable);
                }
            }
            //удаляем таску если она была отправленна с задержкой
            delayedHandler.removeTask(task);

        }

        void removeTasks() {
            removeCallbacksAndMessages(null);
            postRunnables.clear();
            delayedHandler.removeTasks();
        }

        void postDelayedTask(Task task,long millis){
            if(millis==0){
                TaskRunnable taskRunnable=new TaskRunnable(task);
                super.post(taskRunnable);
            }else{
                delayedHandler.postTaskRunnble(new TaskRunnable(task){
                    @Override
                    public void run() {
                        //super.run();
                        postDelayedTask(task,0);
                    }
                },millis);
            }

        }
    }

    private static class DelayedHandler extends Handler{
        private List<TaskRunnable> taskRunnables;

        public DelayedHandler(Looper looper) {
            super(looper);
            taskRunnables =new ArrayList<>();
        }

        @Override
        public void dispatchMessage(Message msg) {
            //убираем выполненую таску из массива тасок
            if(msg.getCallback()!=null){
                TaskRunnable taskRunnable= (TaskRunnable) msg.getCallback();
                Iterator<TaskRunnable> iterator= taskRunnables.iterator();
                while (iterator.hasNext()){
                    TaskRunnable t=iterator.next();
                    if(taskRunnable==t){
                        iterator.remove();
                        break;
                    }
                }
            }
            super.dispatchMessage(msg);
        }

        void removeTask(Task task) {
            Iterator<TaskRunnable> iterator= taskRunnables.iterator();
            while (iterator.hasNext()){
                TaskRunnable taskRunnable=iterator.next();
                if(taskRunnable.task==task){
                    iterator.remove();
                    removeCallbacks(taskRunnable);
                    Log.d("DelayedHandler","task removed:"+taskRunnable);
                }
            }
        }

        void removeTasks() {
            taskRunnables.clear();
            removeCallbacksAndMessages(null);
        }

        public void postTaskRunnble(TaskRunnable taskRunnable,long delay){
            taskRunnables.add(taskRunnable);
            super.postDelayed(taskRunnable,delay);
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
