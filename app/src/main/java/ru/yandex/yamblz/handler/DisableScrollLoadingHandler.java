package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DisableScrollLoadingHandler implements CriticalSectionsHandler, MessageQueue.IdleHandler {
    private final Set<Integer> runningSections = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    private final List<Task> tasks = new CopyOnWriteArrayList<>();
    private final WeakReference<MessageQueue> listenableQueue;

    public DisableScrollLoadingHandler(MessageQueue queue) {
        listenableQueue = new WeakReference<>(queue);
    }

    @Override
    public void startSection(int id) {
        runningSections.add(id);
    }

    @Override
    public void stopSection(int id) {
        if (runningSections.contains(id)) {
            runningSections.remove(id);
        }
        addIdleHandler();
    }

    @Override
    public void stopSections() {
        runningSections.clear();
        addIdleHandler();
    }

    private void runTasks() {
        for (Task task : tasks) {
            if (runningSections.size() == 0) {
                task.run();
                removeLowPriorityTask(task);
            }
        }
    }

    // Вот тут я хз, честно.
    // Вот вроде по дяде Бобу это неправильно написано, а с другой стороны вроде логично выглядит
    private boolean addIdleHandler() {
        if (listenableQueue.get() != null) {
            if (tasks.size() != 0 && !queueIdle()) {
                listenableQueue.get().addIdleHandler(this);
            }
            return true;
        }

        return false;
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (addIdleHandler()) tasks.add(task);
        // throw exception?
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> postLowPriorityTask(task), delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        tasks.remove(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        tasks.clear();
    }

    @Override
    public boolean queueIdle() {
        if (runningSections.size() == 0) runTasks();
        return runningSections.size() == 0 && tasks.size() != 0;
    }
}
