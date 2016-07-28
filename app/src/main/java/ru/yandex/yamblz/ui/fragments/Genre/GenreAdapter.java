package ru.yandex.yamblz.ui.fragments.Genre;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.CollageStrategyImpl;

/**
 * Created by kostya on 26.07.16.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreViewHolder> {
    private List<Genre> genres = new ArrayList<>();
    private FragmentActivity context;

    public GenreAdapter(List<Genre> genres, FragmentActivity context) {
        this.genres = genres;
        this.context = context;
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        Log.d("onBindViewHlder", "genre = " + genre.getName());
        holder.textInList.setText(genre.getName() + "\n" + genre.getDescription());
        CollageLoaderManager.getLoader().loadCollage(
                genre.getImgUrls(),
                holder.imgInList,
                new CollageStrategyImpl()
        );
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }
}
