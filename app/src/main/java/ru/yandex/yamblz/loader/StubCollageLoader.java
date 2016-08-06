package ru.yandex.yamblz.loader;

import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

import ru.yandex.yamblz.handler.CollageCreateTask;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.CollageCreateTaskFinishListener;

public class StubCollageLoader implements CollageLoader, CollageCreateTaskFinishListener {

    private HashMap<Object, CollageCreateTask> tasks = new HashMap<>();

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView, null, new DefaultCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, null, imageTarget, new DefaultCollageStrategy());
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, imageView, null, collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget,
                            CollageStrategy collageStrategy) {
        loadCollage(urls, null, imageTarget, collageStrategy);
    }

    @Override
    public void onTaskFinished(CollageCreateTask task) {
        if (task.getImageView() != null) {
            tasks.remove(task.getImageView());
        } else {
            tasks.remove(task.getImageTarget());
        }
    }

    private void loadCollage(List<String> urls,
                             ImageView imageView,
                             ImageTarget imageTarget,
                             CollageStrategy collageStrategy) {
        CollageCreateTask task = new CollageCreateTask(urls, imageView, imageTarget, collageStrategy);
        task.setListener(this);
        if (imageView != null) {
            if (tasks.containsKey(imageView)) {
                tasks.get(imageView).stop();
                CriticalSectionsManager.getHandler().removeLowPriorityTask(tasks.get(imageView));
            }
            tasks.put(imageView, task);
        } else {
            if (tasks.containsKey(imageTarget)) {
                tasks.get(imageTarget).stop();
                CriticalSectionsManager.getHandler().removeLowPriorityTask(tasks.get(imageTarget));
            }
            tasks.put(imageTarget, task);
        }
        CriticalSectionsManager.getHandler().postLowPriorityTask(task);
    }

}
