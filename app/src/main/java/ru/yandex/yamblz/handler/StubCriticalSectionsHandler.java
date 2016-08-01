package ru.yandex.yamblz.handler;

import android.os.Handler;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private Handler handler;
    private Set<Integer> sections = new HashSet<>();
    private Queue<Task> tasks = new ArrayDeque<>();

    StubCriticalSectionsHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void startSection(int id) {
        sections.add(id);
    }

    @Override
    public void stopSection(int id) {
        sections.remove(id);
    }

    @Override
    public void stopSections() {
        sections.clear();
    }

    @Override
    public void postLowPriorityTask(Task task) {

    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {

    }

    @Override
    public void removeLowPriorityTask(Task task) {

    }

    @Override
    public void removeLowPriorityTasks() {

    }
}
