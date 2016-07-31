package ru.yandex.yamblz.handler;

import android.graphics.Bitmap;
import android.util.Log;

import ru.yandex.yamblz.loader.interfaces.ImageTarget;

/**
 * Created by platon on 29.07.2016.
 */
public class TaskSetImageView implements Task
{
    private static final String TAG = "TaskSetImageView";
    private ImageTarget imageTarget;
    private Bitmap bitmap;
    private int id;

    public TaskSetImageView(int id, ImageTarget imageTarget, Bitmap bitmap)
    {
        this.imageTarget = imageTarget;
        this.bitmap = bitmap;
        this.id = id;
    }

    @Override
    public void run()
    {
        Log.d(TAG, "run");
        imageTarget.onLoadBitmap(bitmap);
    }

    @Override
    public int getId()
    {
        return id;
    }
}
