package ru.yandex.yamblz.ui.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoaderManager;

/**
 * Created by Litun on 07.08.2016.
 */

public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.GenreViewHolder> {

    private List<GenreCovers> data = new ArrayList<>();
    private Context context;

    public GenreListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<GenreCovers> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.genre_cover_item, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class GenreViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView cover;
        @BindView(R.id.text)
        TextView text;

        public GenreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(GenreCovers covers) {
            text.setText(covers.genre);
            cover.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.white)));
            CollageLoaderManager.getLoader().loadCollage(new ArrayList<>(covers.covers), cover);
        }
    }

    public static class GenreCovers {
        public GenreCovers(String genre, Collection<String> covers) {
            this.covers = covers;
            this.genre = genre;
        }

        private String genre;
        private Collection<String> covers;
    }
}
