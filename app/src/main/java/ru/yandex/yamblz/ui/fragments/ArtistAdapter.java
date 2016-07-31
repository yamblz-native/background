package ru.yandex.yamblz.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.CollageStrategyImpl;

import static android.support.v7.appcompat.R.id.image;

/**
 * Created by danil on 25.04.16.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private List<Genre> mDataset;
    private FragmentActivity context;

    public ArtistAdapter(FragmentActivity context) {
        mDataset = new ArrayList<>();
        this.context = context;
    }

    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);

        // change layout's params. here

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    public void setItems(List<Genre> items) {
        if (mDataset != null) {
            int x = mDataset.size();
            mDataset.clear();
            notifyItemRangeRemoved(0, x);
        }
        if (items != null && mDataset != null) {
            mDataset.addAll(items);
            notifyItemRangeInserted(0, items.size());
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.genres.setText(mDataset.get(position).getGenre());
        holder.artists.setText(mDataset.get(position).getNames());
        holder.image.setImageDrawable(null);


        Task createImages = () -> new Thread(() -> {
            CollageLoaderManager.getLoader().loadCollage(mDataset.get(position).getImgUrls(), holder.image, new CollageStrategyImpl());
        }).start();
        CriticalSectionsManager.getHandler().postLowPriorityTask(createImages);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView genres;
        public TextView artists;
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            genres = (TextView) v.findViewById(R.id.recycler_item_genre);
            image = (ImageView) v.findViewById(R.id.recycler_item_image);
            artists  = (TextView) v.findViewById(R.id.recycler_item_artists);
        }
    }
}
