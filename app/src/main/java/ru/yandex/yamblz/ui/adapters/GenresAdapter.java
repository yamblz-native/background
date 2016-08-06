package ru.yandex.yamblz.ui.adapters;

import android.media.tv.TvContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Artist;
import ru.yandex.yamblz.data.Genre;

/**
 * Created by grin3s on 06.08.16.
 */

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenresHolder> {

    List<Genre> genreList = new ArrayList<>();

    @Override
    public GenresHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_element, parent, false);
        final GenresHolder holder = new GenresHolder(view);
        return holder;
    }

    public void changeData(List<Genre> newData) {
        genreList = newData;
        notifyDataSetChanged();
    }

    public void resetData() {
        genreList = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(GenresHolder holder, int position) {
        holder.bind(genreList.get(position));
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    static class GenresHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.genre_textview)
        TextView genreTextView;

        @BindView(R.id.artists_textview)
        TextView artistsTextView;

        public GenresHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Genre genre) {
            genreTextView.setText(genre.getName());
            StringBuilder builder = new StringBuilder();
            List<Artist> artistList = genre.getArtistList();
            for (int i = 0; i < artistList.size() - 1; i++) {
                builder.append(artistList.get(i).getName()).append(", ");
            }
            builder.append(artistList.get(artistList.size()- 1).getName());
            artistsTextView.setText(builder);
        }
    }
}
