package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.yandex.yamblz.genre.RecyclerScrollListener;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.TaskSetImageView;
import ru.yandex.yamblz.loader.interfaces.CollageLoader;
import ru.yandex.yamblz.loader.interfaces.CollageStrategy;
import ru.yandex.yamblz.loader.interfaces.ImageTarget;

import static ru.yandex.yamblz.genre.RecyclerScrollListener.CS_ID;

public class CriticalSectionsCollageLoader implements CollageLoader
{
    private static final int DEF_THREAD_COUNT = 4;
    private static final int TASK_ID = 1991;

    private final CollageStrategy collageStrategy;
    private final LinkedList<ImageTarget> imageTargets;
    private final CriticalSectionsHandler criticalSections;

    public CriticalSectionsCollageLoader()
    {
        collageStrategy = new CollageStrategyImpl();
        imageTargets = new LinkedList<>();
        criticalSections = CriticalSectionsManager.getHandler();
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
        criticalSections.postLowPriorityTask(new TaskSetImageView(CS_ID, imageTarget, bitmap));
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
