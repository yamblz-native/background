package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.Looper;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class CriticalSectionsHandlerImpl implements CriticalSectionsHandler {
    // Why do I even need critical section ids
    private Queue<Integer> sections = new PriorityQueue<>();
    private Stack<Task> taskStack = new Stack<>();

    @Override
    public void startSection(int id) {
        sections.add(id);
    }

    @Override
    public void stopSection(int id) {
        sections.remove(id);
        executeAllTaskIfPossible();
    }

    @Override
    public void stopSections() {
        sections.clear();
        executeAllTaskIfPossible();
    }

    @Override
    public void postLowPriorityTask(Task task) {
        taskStack.push(task);
        executeTaskIfPossible(task);
    }

    @Override
    public void postLowPriorityTaskDelayed(Task task, int delay) {
        new Handler(Looper.myLooper())
                .postDelayed(() -> {
                    taskStack.push(task);
                    executeTaskIfPossible(task);
                }, delay);
    }

    @Override
    public void removeLowPriorityTask(Task task) {
        taskStack.remove(task);
    }

    @Override
    public void removeLowPriorityTasks() {
        taskStack.clear();
    }

    private void executeTaskIfPossible(Task task) {
        if (sections.isEmpty()) {
            taskStack.remove(task);
            task.run();
        }
    }

    private void executeAllTaskIfPossible() {
        while (!taskStack.isEmpty()) {
            Task task = taskStack.pop();
            task.run();
        }
    }
}
