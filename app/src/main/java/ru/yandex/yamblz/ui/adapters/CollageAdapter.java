package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.ui.adapters.CollageAdapter.CollageHolder;
import ru.yandex.yamblz.ui.other.ImageType;

public class CollageAdapter extends Adapter<CollageHolder> {
    private Map<ImageView, WeakReference<Task>> loaderTasks = new WeakHashMap<>();
    private Map<ImageType, int[]> images;

    public CollageAdapter(Map<ImageType, int[]> images) {
        this.images = images;
    }


    static class CollageHolder extends ViewHolder {
        TextView label;
        ImageView collage;

        public CollageHolder(View view) {
            super(view);
            label = (TextView) view.findViewById(R.id.label);
            collage = (ImageView) view.findViewById(R.id.collage);
        }
    }


    @Override
    public CollageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collage_view, parent, false);
        return new CollageHolder(view);
    }


    @Override
    public void onBindViewHolder(CollageHolder holder, int position) {
        ImageType imageType = ImageType.values()[position];

        holder.label.setText(imageType.getLabel());
        holder.collage.setImageBitmap(null);

        CriticalSectionsHandler sectionsHandler = CriticalSectionsManager.getHandler();

        // If there is a pending task for the same image view, remove it
        WeakReference<Task> refTask = loaderTasks.remove(holder.collage);
        if (refTask != null) {
            sectionsHandler.removeLowPriorityTask(refTask.get());
        }

        Task task = () -> CollageLoaderManager.getLoader().loadCollage(images.get(imageType), holder.collage);

        sectionsHandler.postLowPriorityTask(task);

        loaderTasks.put(holder.collage, new WeakReference<>(task));
    }


    @Override
    public int getItemCount() {
        return images.size();
    }
}
