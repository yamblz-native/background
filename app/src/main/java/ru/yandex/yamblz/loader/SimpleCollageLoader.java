package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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
    private final Handler mainThreadHandler;
    private final LinkedList<ImageTarget> imageTargets;

    public SimpleCollageLoader(Handler handler)
    {
        mainThreadHandler = handler;
        collageStrategy = new CollageStrategyImpl();
        imageTargets = new LinkedList<>();
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
        int threadsCount = urls.size() < DEF_THREAD_COUNT ? 1 : DEF_THREAD_COUNT;
        CountDownLatch cdl = new CountDownLatch(threadsCount);
        List<Bitmap> bitmaps = Collections.synchronizedList(new ArrayList<>());
        ExecutorService downloadExecutor = Executors.newFixedThreadPool(threadsCount);

        clearDuplicate(imageTarget.getImageView());
        imageTargets.push(imageTarget);

        for(int i = 0; i < threadsCount; i++)
        {
            ImageDownloader imgDownloader = new ImageDownloader(urls.get(i), bitmaps, cdl);
            downloadExecutor.execute(imgDownloader);
        }

        downloadExecutor.shutdown();

        new CollageConsumer(
                bitmap -> postResult(imageTarget, bitmap),
                bitmaps, strategy, cdl).start();
    }

    private void postResult(ImageTarget imageTarget, Bitmap bitmap)
    {
        mainThreadHandler.post(() -> imageTarget.onLoadBitmap(bitmap));
    }

    private void clearDuplicate(ImageView newImgView)
    {
        if (newImgView != null)
        {
            for (ImageTarget it: imageTargets)
            {
                ImageView cachedImgView = it.getImageView();
                if (cachedImgView!= null && cachedImgView.equals(newImgView)) it.clear();
            }
        }
    }
}
