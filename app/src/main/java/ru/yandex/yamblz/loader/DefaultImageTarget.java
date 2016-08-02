package ru.yandex.yamblz.loader;


import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;

public class DefaultImageTarget implements ImageTarget {
    private WeakReference<ImageView> imageView;

    public DefaultImageTarget(ImageView imageView) {
        this.imageView = new WeakReference<>(imageView);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        if(imageView.get()!=null){
            if(imageView.get().getTag()!=null){
                Task prevTask= (Task) imageView.get().getTag();
                CriticalSectionsManager.getHandler().removeLowPriorityTask(prevTask);
            }
            Task task = () -> {
                if (imageView.get() != null) {
                    imageView.get().setImageBitmap(bitmap);
                }
            };
            imageView.get().setTag(task);
            CriticalSectionsManager.getHandler().postLowPriorityTask(task);
        }
    }

}
