package ru.yandex.yamblz.handler;

import android.graphics.Bitmap;
import android.util.Log;

import ru.yandex.yamblz.loader.interfaces.ImageTarget;

/**
 * Created by platon on 29.07.2016.
 */
public class TaskSetImageView implements Task
{
    private ImageTarget imageTarget;
    private Bitmap bitmap;

    public TaskSetImageView(ImageTarget imageTarget, Bitmap bitmap)
    {
        this.imageTarget = imageTarget;
        this.bitmap = bitmap;
    }

    @Override
    public void run()
    {
        imageTarget.onLoadBitmap(bitmap);
    }
}
