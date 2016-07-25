package ru.yandex.yamblz.handler;

public interface CriticalSectionsHandler {

    void startSection(int id);

    void stopSection(int id);

    void stopSections();

    void postLowPriorityTask(Task task);

    void postLowPriorityTaskDelayed(Task task, int delayMillis);

    void removeLowPriorityTask(Task task);

    void removeLowPriorityTasks();

}
