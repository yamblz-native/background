package ru.yandex.yamblz.loader;


import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;

public class DefaultImageTarget implements ImageTarget {
    private WeakReference<ImageView> imageView;
    Task prevTask;

    public DefaultImageTarget(ImageView imageView) {
        this.imageView = new WeakReference<>(imageView);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        if (prevTask != null) {
            CriticalSectionsManager.getHandler().removeLowPriorityTask(prevTask);
        }
        prevTask = () -> {
            if (imageView.get() != null) {
                imageView.get().setImageBitmap(bitmap);
            }
        };
        CriticalSectionsManager.getHandler().postLowPriorityTask(prevTask);
    }

}
