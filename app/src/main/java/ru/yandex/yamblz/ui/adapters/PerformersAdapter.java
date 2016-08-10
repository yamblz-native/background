package ru.yandex.yamblz.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Genre;
import ru.yandex.yamblz.handler.Task;


/**
 * Created by dalexiv on 4/21/16.
 */
/*
    Adapter for recyclerview in MainActivity
 */
public class PerformersAdapter extends RecyclerView.Adapter<PerformersAdapter.ViewHolder> {
    private List<Genre> dataset;
    private Fragment fragment;
    private ILoadImage iLoadImage;

    public PerformersAdapter(Fragment fragment, ILoadImage iLoadImage) {
        this.fragment = fragment;
        this.iLoadImage = iLoadImage;
        this.dataset = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Genre genre = dataset.get(position);

        // Removing old image
        holder.imageView.setImageResource(0);

//        // Cancelling old subscription
//        final Object subscriptionTag = holder.imageView.getTag();
//        if (subscriptionTag instanceof Subscription) {
//            Subscription sub = (Subscription) subscriptionTag;
//            if (!sub.isUnsubscribed())
//                sub.unsubscribe();
//        }

        // Removing old tasks
        final Object taskTag = holder.imageView.getTag();
        if (taskTag instanceof Task) {
            iLoadImage.removeOldTask((Task) taskTag);
        }

        // Just redirect call to presenter
        iLoadImage.postDownloadingTask(genre, holder.imageView);
        // Setting various text fields
        holder.textViewName.setText(genre.getName());
    }

    public void addGenre(Genre genre) {
        dataset.add(genre);

        // Notify about added element
        notifyItemInserted(dataset.size() - 1);
    }

    public void clearPerformers() {
        // Just redirect call to presenter
        dataset.clear();

        // Notify about removed elements
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /*
        Performer's viewModel
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewName;
        public TextView textViewGenre;
        public TextView textViewStats;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.performerImage);
            textViewName = (TextView) v.findViewById(R.id.performerName);
            textViewGenre = (TextView) v.findViewById(R.id.performerGenre);
            textViewStats = (TextView) v.findViewById(R.id.performerStats);
        }

    }

}
