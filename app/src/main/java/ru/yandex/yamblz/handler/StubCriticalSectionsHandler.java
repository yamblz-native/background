package ru.yandex.yamblz.handler;

public class StubCriticalSectionsHandler implements CriticalSectionsHandler {

    @Override
    public void startSection(int id) {

    }

    @Override
    public void stopSection(int id) {

    }

    @Override
    public void stopSections() {

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
