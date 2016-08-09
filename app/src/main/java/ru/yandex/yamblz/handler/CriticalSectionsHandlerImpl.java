package ru.yandex.yamblz.handler;


import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CriticalSectionsHandlerImpl implements CriticalSectionsHandler {
    private Handler mHandler;
    private Queue<Task> mTaskQueue;
    private SparseIntArray mSectionArray;

    public CriticalSectionsHandlerImpl() {
        mHandler = new Handler(Looper.getMainLooper());
        mTaskQueue = new LinkedBlockingQueue<>();
        mSectionArray = new SparseIntArray();
    }

    @Override
    public void startSection(int id) {
        mSectionArray.append(id, 1);
    }

    @Override
    public void stopSection(int id) {
        mSectionArray.delete(id);
        doNext();
    }


    @Override
    public void stopSections() {
        mSectionArray.clear();
        doNext();
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (mSectionArray.size() != 0) {
            mTaskQueue.add(task);
        } else {
            mHandler.post(task::run);
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        if (mSectionArray.size() != 0) {
            mTaskQueue.add(task);
        } else {
            mHandler.postDelayed(task::run, delay);
        }
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        mHandler.removeCallbacks(task::run);
    }

    @Override
    public void removeLowPriorityTasks() {
        while (!mTaskQueue.isEmpty()) {
            mHandler.removeCallbacks(mTaskQueue.poll()::run);
        }
    }

    private void doNext() {
        if (mSectionArray.size() == 0) {
            Task task = mTaskQueue.poll();
            if (task != null) {
                Task anotherTask = () -> {
                    task.run();
                    doNext();
                };
                mHandler.post(anotherTask::run);
            }
        }
    }
}
