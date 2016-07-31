package ru.yandex.yamblz.loader;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Subscribe/unsubscribe mechanism
 */
public final class Subscription {
    private final AtomicBoolean subscribed;

    public Subscription() {
        subscribed = new AtomicBoolean(true);
    }

    public void unsubscribe() {
        subscribed.set(false);
    }

    public boolean isSubscribed() {
        return subscribed.get();
    }
}