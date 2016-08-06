package ru.yandex.yamblz.handler;

public interface CriticalSectionsHandler {

    void startSection(int id);

    void stopSection(int id);

    void stopSections();

    Task postLowPriorityTask(Task task);

    Task postLowPriorityTaskDelayed(Task task, int delay);

    void removeLowPriorityTask(Task task);

    void removeLowPriorityTasks();

}
