package ru.yandex.yamblz.handler;

public interface CriticalSectionsHandler {

    //пока работает секция ло не работают
    void startSection(int id);

    void stopSection(int id);

    void stopSections();

    void postLowPriorityTask(Task task);

    void postLowPriorityTaskDelayed(Task task, int delay);

    void removeLowPriorityTask(Task task);

    void removeLowPriorityTasks();

}
