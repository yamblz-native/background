package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.MyCollageStrategy;
import ru.yandex.yamblz.loader.MyImageViewTarget;
import ru.yandex.yamblz.models.Genre;
import rx.Subscription;

/**
 * Created by SerG3z on 02.08.16.
 */

public class GenreRecyclerViewAdapter extends RecyclerView.Adapter<GenreRecyclerViewAdapter.ViewHolder> {

    private List<Genre> genreList;

    public GenreRecyclerViewAdapter() {
        genreList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public void addAllData(List<Genre> tmpListGenre) {
        genreList.addAll(tmpListGenre);
        notifyDataSetChanged();
    }

    public Genre getItem(int position) {
        return genreList.get(position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Subscription subscription = holder.getSubscription();
        if (subscription != null) {
            subscription.unsubscribe();
        }
        Task task = holder.getTask();
        if (task != null) {
            CriticalSectionsManager.getHandler().removeLowPriorityTask(task);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.genres_image_view)
        ImageView genreImageView;
        @BindView(R.id.artists_text_view)
        TextView artistList;
        @BindView(R.id.genres_text_view)
        TextView genreTextView;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        private Subscription subscription;
        private Task task;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            Genre genre = getItem(position);
            genreImageView.setImageBitmap(null);
            genreTextView.setText(genre.getGenre());
            artistList.setText(genre.getListArtist().toString());

            task = CriticalSectionsManager.getHandler().postLowPriorityTask(() ->
                    subscription = CollageLoaderManager
                            .getLoader()
                            .loadCollage(genre.getImageUrls(),
                                    new MyImageViewTarget(genreImageView), new MyCollageStrategy()));
        }

        Subscription getSubscription() {
            return subscription;
        }

        Task getTask() {
            return task;
        }
    }
}
