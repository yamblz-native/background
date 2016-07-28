package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;

import ru.yandex.yamblz.handler.Task;
import rx.Subscription;

public interface ImageTarget {

    void onLoadBitmap(Bitmap bitmap);

    void setSubscription(Subscription subscription);

    void setTask(Task task);
}
