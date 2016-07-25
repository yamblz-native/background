package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageStrategy;
import ru.yandex.yamblz.models.Genre;
import ru.yandex.yamblz.loader.Subscription;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder> {

    private List<Genre> mGenres;
    private CollageLoader mCollageLoader;
    private CollageStrategy mCollageStrategy;

    public GenresAdapter(List<Genre> genres, CollageLoader collageLoader, CollageStrategy collageStrategy) {
        mGenres = genres;
        mCollageLoader = collageLoader;
        mCollageStrategy = collageStrategy;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Genre genre = mGenres.get(position);
        Subscription subscription = (Subscription) holder.collage.getTag();
        if(subscription != null) {
            subscription.unsubscribe();
        }
        holder.collage.setImageDrawable(null);
        subscription = mCollageLoader.loadCollage(shuffleAndCut(Genre.getCoversForCollage(genre)),
                holder.collage, mCollageStrategy);
        holder.genre.setText(genre.getGenre());
        holder.singers.setText(TextUtils.join(", ", genre.getSingers()));
        holder.collage.setTag(subscription);
    }

    private List<String> shuffleAndCut(List<String> urls) {
        Collections.shuffle(urls);
        urls = urls.subList(0, Math.min(urls.size(), 4));
        return urls;
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView collage;
        TextView genre, singers;

        public ViewHolder(View itemView) {
            super(itemView);
            collage = (ImageView)itemView.findViewById(R.id.collage);
            genre = (TextView)itemView.findViewById(R.id.genre);
            singers = (TextView)itemView.findViewById(R.id.singers);
        }
    }
}
