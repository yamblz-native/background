package ru.yandex.yamblz.ui.adapters;

import android.graphics.Bitmap;
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
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageStrategy;
import ru.yandex.yamblz.loader.ImageTarget;
import ru.yandex.yamblz.loader.Subscription;
import ru.yandex.yamblz.models.Genre;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.ViewHolder> {

    private List<Genre> mGenres;
    private CollageLoader mCollageLoader;
    private CollageStrategy mCollageStrategy;
    private CriticalSectionsHandler mCriticalSectionHandler;

    public GenresAdapter(List<Genre> genres, CollageLoader collageLoader,
                         CollageStrategy collageStrategy, CriticalSectionsHandler criticalSectionsHandler) {
        this.mGenres = genres;
        this.mCollageLoader = collageLoader;
        this.mCollageStrategy = collageStrategy;
        this.mCriticalSectionHandler = criticalSectionsHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_card, parent, false);
        return new ViewHolder(view, mCriticalSectionHandler);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Genre genre = mGenres.get(position);
        if(holder.subscription != null) {
            //unsubscribe if there was a subscription, because we don't need the result of it
            holder.subscription.unsubscribe();
        }
        holder.subscription = mCollageLoader.loadCollage(shuffleAndCut(Genre.getCoversForCollage(genre)),
                holder, mCollageStrategy);

        //setting to null, just to see how images aren't being loaded while scrolling
        holder.collage.setImageBitmap(null);
        holder.genreTV.setText(genre.getGenre());
        holder.singersTV.setText(TextUtils.join(", ", genre.getSingers()));
    }

    /**
     * Returns first <= 4 urls
     * @param urls urls
     * @return decreased list
     */
    private List<String> shuffleAndCut(List<String> urls) {
        Collections.shuffle(urls);
        urls = urls.subList(0, Math.min(urls.size(), 4));
        return urls;
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ImageTarget {

        ImageView collage;
        TextView genreTV, singersTV;
        CriticalSectionsHandler criticalSectionsHandler;
        Subscription subscription;

        public ViewHolder(View itemView, CriticalSectionsHandler criticalSectionsHandler) {
            super(itemView);
            this.collage = (ImageView)itemView.findViewById(R.id.collage);
            this.genreTV = (TextView)itemView.findViewById(R.id.genre);
            this.singersTV = (TextView)itemView.findViewById(R.id.singers);
            this.criticalSectionsHandler = criticalSectionsHandler;
        }

        @Override
        public void onLoadBitmap(Bitmap bitmap) {
            //post to critical section handler so not to load main thread with setting images
            criticalSectionsHandler.postLowPriorityTask(() -> collage.setImageBitmap(bitmap));
        }
    }
}
