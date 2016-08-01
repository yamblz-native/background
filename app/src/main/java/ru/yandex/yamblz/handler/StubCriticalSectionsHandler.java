package ru.yandex.yamblz.handler;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final Handler delayHandler;

    private final Set<Integer> criticalSectionStore = new HashSet<>();
    private final Queue<Task> taskStore = new ArrayDeque<>();

    public StubCriticalSectionsHandler() {
        HandlerThread thread = new HandlerThread("delayThread");
        thread.start();
        delayHandler = new Handler(thread.getLooper());
    }

    @Override
    public void startSection(int id) {
        criticalSectionStore.add(id);
    }

    @Override
    public void stopSection(int id) {
        criticalSectionStore.remove(id);
    }

    @Override
    public void stopSections() {
        criticalSectionStore.clear();
    }

    @Override
    public void postLowPriorityTask(Task task) {
        taskStore.add(task);
        runTasks();
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        delayHandler.postDelayed(()-> postLowPriorityTask(task), delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        taskStore.remove(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        taskStore.clear();
    }

    private void runTasks(){
        mainThreadHandler.post(() -> {
                while(canRunNextTask()) {
                    taskStore.poll().run();
                }
        });

    }

    private boolean canRunNextTask(){
        return criticalSectionStore.isEmpty()
                && !taskStore.isEmpty();
    }
}
