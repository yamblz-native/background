package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.model.Genre;

public class GenresListAdapter extends RecyclerView.Adapter<GenresListAdapter.GenreViewHolder> {

    private List<Genre> genres;
    CollageLoader collageLoader;

    public GenresListAdapter(List<Genre> genres, CollageLoader collageLoader) {
        this.genres = genres;
        this.collageLoader = collageLoader;
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genres_list, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);

        holder.tvGenre.setText(genre.getName());
        holder.ivCollage.setImageResource(R.drawable.cover_placeholder_small);

        List<String> urls = new ArrayList<>();
        Random random = new Random();

        if (genre.getUrls().size() < 4) {
            urls.add(genre.getUrls().get(random.nextInt(genre.getUrls().size())));
        } else {
            for (int i = 0; i < 4; i++) {
                urls.add(genre.getUrls().get(genre.getUrls().size()*i/4 + random.nextInt(genre.getUrls().size()/4)));
            }
        }

        collageLoader.loadCollage(urls, holder.ivCollage);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCollage;
        TextView tvGenre;

        public GenreViewHolder(View itemView) {
            super(itemView);

            ivCollage = (ImageView) itemView.findViewById(R.id.iv_collage);
            tvGenre = (TextView) itemView.findViewById(R.id.tv_genre);
        }

    }

}
