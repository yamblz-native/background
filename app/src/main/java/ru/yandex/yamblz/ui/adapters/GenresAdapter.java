package ru.yandex.yamblz.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.tv.TvContract;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.AbstractList;
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
import ru.yandex.yamblz.data.Artist;
import ru.yandex.yamblz.data.Genre;
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
        holder.bind(genreList.get(position));
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenresHolder extends RecyclerView.ViewHolder implements ImageTarget {
        @Inject @Named(ApplicationModule.MAIN_THREAD_POOL_EXECUTOR)
        ThreadPoolExecutor executor;

        @Inject @Named(ApplicationModule.MAIN_THREAD_HANDLER)
        Handler mainThreadHandler;

        @BindView(R.id.genre_textview)
        TextView genreTextView;

        @BindView(R.id.artists_textview)
        TextView artistsTextView;

        @BindView(R.id.genre_imageview)
        ImageView genreImageView;

        CollageLoader currentAsyncLoader;

        int counter = 0;

        public GenresHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            App.get(context).applicationComponent().inject(this);
        }

        void bind(Genre genre) {
            Log.d("BIND", "bind");
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
            currentAsyncLoader = new ThreadedCollageLoader(executor, mainThreadHandler);
            currentAsyncLoader.loadCollage(imageUrls, this);
        }

        @Override
        public void onLoadBitmap(Bitmap bitmap, AsyncLoader asyncLoader) {
            counter++;
            Log.d("COUNTER", Integer.toString(counter));
            genreImageView.setImageBitmap(bitmap);
        }
    }

}
