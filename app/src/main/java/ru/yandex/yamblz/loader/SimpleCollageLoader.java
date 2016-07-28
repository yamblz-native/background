package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.yandex.yamblz.loader.interfaces.CollageLoader;
import ru.yandex.yamblz.loader.interfaces.CollageStrategy;
import ru.yandex.yamblz.loader.interfaces.ImageTarget;

public class SimpleCollageLoader implements CollageLoader
{
    private static final int DEF_THREAD_COUNT = 4;

    private final CollageStrategy collageStrategy;
    private Handler mainThreadHandler;

    public SimpleCollageLoader(Handler mainThreadHandler)
    {
        this.mainThreadHandler = mainThreadHandler;
        collageStrategy = new CollageStrategyImpl();
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView)
    {
        ImageTarget imageTarget = new ImageTargetImpl(imageView);
        loadCollage(urls, imageTarget);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget)
    {
        loadCollage(urls, imageTarget, collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy strategy)
    {
        ImageTarget imageTarget = new ImageTargetImpl(imageView);
        loadCollage(urls, imageTarget, strategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy strategy)
    {
        int threadCount = urls.size() < DEF_THREAD_COUNT ? 1 : DEF_THREAD_COUNT;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        List<Bitmap> bitmaps = Collections.synchronizedList(new ArrayList<>());

        for(int i = 0; i < threadCount; i++)
        {
            executorService.submit(new ImageDownloader(urls.get(i), bitmaps, countDownLatch));
        }
        executorService.shutdown();
        new Consumer(() -> postResult(bitmaps, imageTarget, strategy), countDownLatch).start();
    }

    private void postResult(List<Bitmap> bitmaps, ImageTarget imageTarget, CollageStrategy strategy)
    {
        mainThreadHandler.post(() -> {
            Bitmap collage = strategy.create(bitmaps);
            imageTarget.onLoadBitmap(collage);
        });
    }
}
