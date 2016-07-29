package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.DefaultCollageLoader;
import ru.yandex.yamblz.ui.adapters.CollageAdapter.CollageHolder;
import ru.yandex.yamblz.ui.other.ImageType;

public class CollageAdapter extends Adapter<CollageHolder> {
    private Map<ImageType, int[]> images;

    public CollageAdapter(Map<ImageType, int[]> images) {
        this(images, null);
    }


    public CollageAdapter(Map<ImageType, int[]> images, CollageLoader loader) {
        this.images = images;
        CollageLoaderManager.init((loader == null) ? new DefaultCollageLoader() : loader);
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

        CollageLoaderManager.getLoader().loadCollage(images.get(imageType), holder.collage);
    }


    @Override
    public int getItemCount() {
        return images.size();
    }
}
