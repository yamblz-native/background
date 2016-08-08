package ru.yandex.yamblz.ui.adapters;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Cover;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Volha on 04.08.2016.
 */

public class GenresRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Map<String, List<Cover>> genres = new HashMap<>();
    List<String> keys;
    Map<String, Subscription> subscriptions = new HashMap<>();
    LruCache<String,Bitmap> cache = new LruCache<>(1024 * 10);

    public GenresRecyclerAdapter() {
    }

    public void setItems(Map<String, List<Cover>> genres) {
        if ( genres != null ) {
            this.genres.clear();
            this.genres.putAll(genres);
            keys = new ArrayList<>(genres.keySet());
        } else
            keys = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_genre_item, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if ( holder instanceof GenreViewHolder ) {
            GenreViewHolder genre = (GenreViewHolder) holder;
            genre.title.setText( keys.get(position) );
            genre.progress.setVisibility(View.VISIBLE);
            genre.cover.setVisibility(View.GONE);
            if (genre.subscription != null) {
                genre.subscription.unsubscribe();
            }
            if (cache.get(keys.get(position)) != null) { // загружаем из кэша
                genre.cover.setImageBitmap(cache.get(keys.get(position)));
                genre.cover.setVisibility(View.VISIBLE);
                genre.progress.setVisibility(View.GONE);
            }
            genre.subscription = loadCovers(genre, position); // обновляем картинку всё равно (вдруг изменилась)
        }
    }

    private Subscription loadCovers(GenreViewHolder genre, int position) {
        return rx.Observable
                .from(getItemByPosition(position))
                .observeOn(Schedulers.io())
                .flatMap(cover -> loadImage( cover.getSmall() ))
                .doOnError(throwable -> { Log.d("loadError", throwable.getMessage()); })
                .subscribeOn(Schedulers.computation())
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmaps -> {
                    Bitmap collage = makeCollage((ArrayList<Bitmap>) bitmaps);
                    genre.cover.setImageBitmap(collage);
                    genre.cover.setVisibility(View.VISIBLE);
                    genre.progress.setVisibility(View.GONE);
                    cache.put(keys.get(position), collage);
                });
    }

    private Bitmap makeCollage(ArrayList<Bitmap> bitmaps) {

        if ( bitmaps.size() == 3 )
            return draw3Covers(bitmaps);

        int columns = getColumnCount(bitmaps.size());
        int rows = bitmaps.size() / columns;

        int itemWidth = bitmaps.get(0).getWidth();
        int width = itemWidth * columns;
        int itemHeight = bitmaps.get(0).getHeight();
        int height = itemHeight * rows;

        Bitmap collage = Bitmap.createBitmap(width, height, bitmaps.get(0).getConfig());
        Canvas canvas = new Canvas(collage);

        for ( int i = 0; i < rows; ++i ) {
            for (int j = 0; j < columns; j++) {
                if ( bitmaps.size() > i*rows + j ) {
                    Bitmap bitmap = bitmaps.get(i * rows + j);
                    canvas.drawBitmap(bitmap, j * itemWidth, i * itemHeight, null);
                }
            }
        }

        return collage;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Bitmap draw3Covers(ArrayList<Bitmap> bitmaps) {

        int itemWidth = bitmaps.get(0).getWidth();
        int width = itemWidth * 2;
        int itemHeight = bitmaps.get(0).getHeight();
        int height = itemHeight * 2;

        Bitmap collage = Bitmap.createBitmap(width, height, bitmaps.get(0).getConfig());
        Canvas canvas = new Canvas(collage);

        canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
        canvas.drawBitmap(bitmaps.get(1), 0, itemHeight, null);
        bitmaps.set(2, (Bitmap.createScaledBitmap(bitmaps.get(2), itemWidth, height, false)) );
        canvas.drawBitmap(bitmaps.get(2), itemWidth, 0, null);

        return collage;
    }

    private int getColumnCount(int size) {
        return (int) Math.ceil(Math.sqrt(size));
    }

    private Observable<Bitmap> loadImage(String url) {
        return Observable.create(subscriber -> {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(url, new ImageSize(100, 100), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    subscriber.onNext(loadedImage);
                    subscriber.onCompleted();
                }
            });
        });
    }

    public List<Cover> getItemByPosition(int position) {
        return genres.get(keys.get(position));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.genre_title)
        TextView title;
        @BindView(R.id.genre_cover)
        ImageView cover;
        @BindView((R.id.genre_progress))
        ProgressBar progress;
        Subscription subscription;

        public GenreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
