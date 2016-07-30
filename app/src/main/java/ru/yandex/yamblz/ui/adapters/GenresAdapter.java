package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.models.Genre;

/**
 * Created by shmakova on 29.07.16.
 */

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenreViewHolder> {
    private List<Genre> genres;

    public GenresAdapter(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genres, parent, false);
        return new GenreViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.name.setText(genre.getName());
        holder.collage.setImageResource(android.R.color.transparent);
        CollageLoaderManager.getLoader().loadCollage(genre.getUrls(), holder.collage);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.collage)
        ImageView collage;
        @BindView(R.id.name)
        TextView name;

        public GenreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}