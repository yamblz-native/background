package ru.yandex.yamblz.ui.adapters;

import android.media.tv.TvContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Genre;

/**
 * Created by grin3s on 06.08.16.
 */

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenresHolder> {

    List<Genre> genreList = new ArrayList<Genre>();

    @Override
    public GenresHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_element, parent, false);
        final GenresHolder holder = new GenresHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GenresHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class GenresHolder extends RecyclerView.ViewHolder {
        public GenresHolder(View itemView) {
            super(itemView);
        }
    }
}
