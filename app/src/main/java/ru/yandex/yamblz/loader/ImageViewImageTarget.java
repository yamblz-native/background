package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.Task;
import rx.Subscription;

/**
 * Created by Aleksandra on 27/07/16.
 */
public class ImageViewImageTarget implements ImageTarget {
    private WeakReference<ImageView> reference;

    public ImageViewImageTarget(WeakReference<ImageView> reference) {
        this.reference = reference;
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        final ImageView iv = reference.get();
        if (iv != null) {
            Log.d("qq", iv.getParent().toString());
            iv.setImageBitmap(bitmap);
            iv.invalidate();
        }
    }

    @Override
    public void setSubscription(Subscription subscription) {
        final ImageView iv = reference.get();
        if (iv != null) {
            iv.setTag(R.id.tag_subscription, subscription);
        }
    }

    @Override
    public void setTask(Task task) {
        final ImageView iv = reference.get();
        if (iv != null) {
            iv.setTag(R.id.tag_task, task);
        }
    }
}
