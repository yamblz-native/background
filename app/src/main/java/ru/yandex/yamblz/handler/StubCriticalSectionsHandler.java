package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    private Set<Integer> sections = new HashSet<>();
    private Set<Task> tasks = new HashSet<>();
    private Handler handler = new Handler();

    @Override
    public void startSection(int id) {
        sections.add(id);
    }

    @Override
    public void stopSection(int id) {
        sections.remove(id);

        if (sections.isEmpty()) {
            for (Task task : tasks) {
                task.run();
            }
            tasks.clear();
        }
    }

    @Override
    public void stopSections() {
        sections.clear();

        for (Task task : tasks) {
            task.run();
        }
        tasks.clear();
    }

    @Override
    public void postLowPriorityTask(Task task) {
        if (sections.isEmpty()) {
            task.run();
        } else {
            tasks.add(task);
        }
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        if (delay <= 0) {
            postLowPriorityTask(task);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    postLowPriorityTask(task);
                }
            }, delay);
        }
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        tasks.remove(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        tasks.clear();
    }

}
