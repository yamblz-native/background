package ru.yandex.yamblz.handler;


public interface UIThreadExecutor {
    void postOnUIThread(Task task);
}
