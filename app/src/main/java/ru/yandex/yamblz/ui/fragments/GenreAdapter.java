package ru.yandex.yamblz.ui.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Genre;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import timber.log.Timber;

import static java.lang.Thread.sleep;

/**
 * Created by aleien on 31.07.16.
 */

class GenreAdapter extends RecyclerView.Adapter {
    private final static int PLACEHOLDER = 0;
    private final static int GENRE = 1;
    private CollageLoader imagesLoader;

    private List<Genre> genres = new ArrayList<>();

    GenreAdapter() {
        imagesLoader = CollageLoaderManager.getLoader();
    }

    public void setContent(List<Genre> genreList) {
        if (genreList != null && genreList.size() != 0) {
            this.genres.addAll(genreList);
        }

        notifyDataSetChanged();
    }

    public void reset() {
        imagesLoader.destroyAll();
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PLACEHOLDER:
                View placeholder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_placeholder, parent, false);
                return new ProgressHolder(placeholder);
            case GENRE:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artists_genre, parent, false);
                return new GenreHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GenreHolder) {
            GenreHolder genreHolder = (GenreHolder) holder;
            genreHolder.genre.setText(genres.get(position).getName());
            genreHolder.image.setImageResource(R.color.d2m_transparent);
            imagesLoader.loadCollage(genres.get(position).getCollageUrls(), genreHolder.image);
        }
    }

    @Override
    public int getItemCount() {
        return isEmpty() ? 1 : genres.size();
    }

    private boolean isEmpty() {
        return genres.size() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty() && position == 0) return PLACEHOLDER;
        return GENRE;
    }


    static class GenreHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_image)
        ImageView image;
        @BindView(R.id.item_genres)
        TextView genre;

        GenreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class ProgressHolder extends RecyclerView.ViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }
    }
}
