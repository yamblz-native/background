package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.MainThread;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Implementation of {@link CriticalSectionsHandler} which uses {@link HandlerThread} for delayed tasks
 * internally
 */
public class CriticalSectionsHandlerThread implements CriticalSectionsHandler {

    //UI thread handler, or any other thread actually
    private Handler mPostHandler;

    //Set of sections
    private Set<Integer> mSections = new HashSet<>();
    //Queue of tasks
    private Queue<Task> mTasks = new ArrayDeque<>();

    //Worker handler for delayed tasks
    private Handler mWorkerHandler;

    @MainThread
    public CriticalSectionsHandlerThread(Handler postHandler) {
        this.mPostHandler = postHandler;
        initWorkerHandler();
    }

    private void initWorkerHandler() {
        HandlerThread handlerThread = new HandlerThread("worker");
        handlerThread.start();
        this.mWorkerHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    @MainThread
    public void startSection(int id) {
        mSections.add(id);
    }

    @Override
    @MainThread
    public void stopSection(int id) {
        mSections.remove(id);
        startTasksIfCan();
    }

    /**
     * Starts tasks if no critical section
     */
    @MainThread
    private void startTasksIfCan() {
        if (!isInCriticalSection()) {
            startTasks();
        }
    }

    /**
     * Starts tasks
     */
    @MainThread
    private void startTasks() {
        while (!mTasks.isEmpty()) {
            //Why post? So not to load main thread
            mPostHandler.post(mTasks.poll()::run);
        }
    }

    /**
     * Checks whether there are any critical sections
     * @return
     */
    @MainThread
    private boolean isInCriticalSection() {
        return mSections.size() != 0;
    }

    @Override
    @MainThread
    public void stopSections() {
        mSections.clear();
        startTasks();
    }

    @Override
    @MainThread
    public void postLowPriorityTask(Task task) {
        mTasks.add(task);
        startTasksIfCan();
    }

    @Override
    @MainThread
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        //first post to worker thread with delay, then post to main thread to queue
        mWorkerHandler.postDelayed(() -> mPostHandler.post(() -> postLowPriorityTask(task)), delay);
    }

    @Override
    @MainThread
    public void removeLowPriorityTask(Task task) {
        mTasks.remove(task);
    }

    @Override
    @MainThread
    public void removeLowPriorityTasks() {
        mTasks.clear();
    }
}
