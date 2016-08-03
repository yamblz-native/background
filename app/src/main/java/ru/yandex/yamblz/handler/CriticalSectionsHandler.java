package ru.yandex.yamblz.handler;

import android.os.MessageQueue;

public interface CriticalSectionsHandler extends MessageQueue.IdleHandler  {

    void startSection(int id);

    void stopSection(int id);

    void stopSections();

    void postLowPriorityTask(Task task);

    void postLowPriorityTaskDelayed(Task task, int delay);

    void removeLowPriorityTask(Task task);

    void removeLowPriorityTasks();

}
