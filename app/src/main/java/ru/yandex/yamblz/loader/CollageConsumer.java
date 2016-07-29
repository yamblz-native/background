package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import ru.yandex.yamblz.loader.interfaces.CollageStrategy;

/**
 * Created by platon on 29.07.2016.
 */
public class CollageConsumer extends Thread
{
    private Callback callback;
    private CountDownLatch countDownLatch;
    private CollageStrategy collageStrategy;
    private List<Bitmap> bitmaps;

    public interface Callback
    {
        void postCollage(Bitmap bitmap);
    }

    public CollageConsumer(Callback callback, List<Bitmap> bitmaps, CollageStrategy strategy, CountDownLatch cdl)
    {
        this.countDownLatch = cdl;
        this.collageStrategy = strategy;
        this.bitmaps = bitmaps;
        this.callback = callback;
    }

    @Override
    public void run()
    {
        super.run();
        try
        {
            countDownLatch.await();
            Bitmap bitmap = collageStrategy.create(bitmaps);
            callback.postCollage(bitmap);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
