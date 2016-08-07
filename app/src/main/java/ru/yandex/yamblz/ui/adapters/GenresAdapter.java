package ru.yandex.yamblz.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.App;
import ru.yandex.yamblz.ApplicationModule;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.images.ImageCache;
import ru.yandex.yamblz.models.Artist;
import ru.yandex.yamblz.models.Genre;
import ru.yandex.yamblz.loader.AsyncLoader;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.ImageTarget;
import ru.yandex.yamblz.loader.ThreadedCollageLoader;

/**
 * Created by grin3s on 06.08.16.
 */

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenresHolder> {

    List<Genre> genreList = new ArrayList<>();

    Context mContext;

    public GenresAdapter(Context context) {
        mContext = context;
    }

    @Override
    public GenresHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_element, parent, false);
        final GenresHolder holder = new GenresHolder(view, mContext);
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
        holder.bind(genreList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenresHolder extends RecyclerView.ViewHolder implements ImageTarget {
        private static final String COLLAGE_CACHE_KEY_TEMPLATE = "collage:";

        @Inject @Named(ApplicationModule.IMAGE_CACHE)
        ImageCache imageCache;

        @Inject @Named(ApplicationModule.THREAD_POOL_EXECUTOR)
        ThreadPoolExecutor executor;

        @Inject @Named(ApplicationModule.MAIN_THREAD_HANDLER)
        Handler mainThreadHandler;

        @Inject @Named(ApplicationModule.MAIN_THREAD_CRITICAL_SECTIONS_HANDLER)
        CriticalSectionsHandler criticalSectionsHandler;

        @BindView(R.id.genre_textview)
        TextView genreTextView;

        @BindView(R.id.artists_textview)
        TextView artistsTextView;

        @BindView(R.id.genre_imageview)
        ImageView genreImageView;

        CollageLoader currentAsyncLoader;

        public GenresHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            App.get(context).applicationComponent().inject(this);
        }

        void bind(Genre genre, int position) {
            genreTextView.setText(genre.getName());
            StringBuilder builder = new StringBuilder();
            List<Artist> artistList = genre.getArtistList();
            for (int i = 0; i < artistList.size() - 1; i++) {
                builder.append(artistList.get(i).getName()).append(", ");
            }
            builder.append(artistList.get(artistList.size()- 1).getName());
            artistsTextView.setText(builder);

            //obtaining urls
            List<String> imageUrls = new ArrayList<>();
            for (Artist artist : artistList) {
                imageUrls.add(artist.getCover().getSmall());
            }

            genreImageView.setImageBitmap(null);

            //here we use the trick from https://developer.android.com/training/displaying-bitmaps/process-bitmap.html
            //remembering the AsyncLoader that was last and comparing it to the one that finished in onLoadBitmap

            currentAsyncLoader = new ThreadedCollageLoader(executor, mainThreadHandler, imageCache);
            currentAsyncLoader.loadCollage(imageUrls, this);
        }

        @Override
        public void onLoadBitmap(Bitmap bitmap, AsyncLoader asyncLoader) {
            if (asyncLoader == currentAsyncLoader) {
                criticalSectionsHandler.postLowPriorityTask(() -> genreImageView.setImageBitmap(bitmap));
            }
        }
    }

}
