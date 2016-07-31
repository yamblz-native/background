package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Artist;
import ru.yandex.yamblz.data.Genre;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static java.lang.Thread.sleep;

/**
 * Created by aleien on 31.07.16.
 */

public class GenreAdapter extends RecyclerView.Adapter {
    public final static int PLACEHOLDER = 0;
    public final static int GENRE = 1;

    CompositeSubscription subs = new CompositeSubscription();

    List<Genre> genres = new ArrayList<>();
    private Context context;

    public GenreAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setContent(List<Genre> genreList) {
        if (genreList != null && genreList.size() != 0) {
            this.genres.addAll(genreList);
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PLACEHOLDER:
                View placeholder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_placeholder, parent, false);
                return new PlaceHolder(placeholder);
            case GENRE:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artists_genre, parent, false);
                return new GenreHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GenreHolder) {
            GenreHolder genreHolder = (GenreHolder) holder;
            genreHolder.genre.setText(genres.get(position).getName());
            genreHolder.image.setImageResource(R.color.d2m_transparent);
            loadCollage(genres.get(position), genreHolder.image);
        }
    }

    private void loadCollage(Genre genre, ImageView image) {
        List<String> strings = genre.getCollageUrls();
        if (strings.size() == 2) {
            subs.add(Observable.<Bitmap, Bitmap, Bitmap>zip(loadBitmap(strings.get(0)),
                    loadBitmap(strings.get(1)),
                    (s1, s2) -> s1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
//                                Timber.e(result);
                                image.setImageBitmap(result);
//                                Glide.with(context)
//                                        .load(result)
//                                        .into(image);
                            },
                            Throwable::printStackTrace
                    ));
        }


//        subs.add(rx.Observable.zip(getImageObservables(genre), new FuncN<String>() {
//            @Override
//            public String call(String... args) {
//                StringBuffer combination = new StringBuffer();
//                for (String s: args) {
//                    combination.append(s);
//                }
//                return combination.toString();
//            }
//        }));
    }

    @NonNull
    private String concatStrings(String[] args) {
        StringBuffer combination = new StringBuffer();
        for (String s : args) {
            combination.append(s);
        }
        return combination.toString();
    }

    private Observable<String> getUrlSingle(String string) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Timber.d(Thread.currentThread().getName());
                try {
                    sleep((long) (Math.random() * 5000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(string);
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Bitmap> loadBitmap(String url) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Timber.d(Thread.currentThread().getName());
                Target<Bitmap> bitmapTarget = new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        subscriber.onNext(resource);
                        subscriber.onCompleted();
                    }
                };
                try {
                    Bitmap bitmap = Glide.with(context)
                            .load(url)
                            .asBitmap()
                            .into(100, 100)
                            .get();
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

            }
        });
    }

    private List<Single<String>> getImageObservables(Genre genre) {
        List<Single<String>> observables = new ArrayList<>();
        List<String> genreArtists = genre.getCollageUrls();

        for (String url : genreArtists) {
            observables.add(Single.create(singleSubscriber -> {
                Timber.d(Thread.currentThread().getName());
                try {
                    wait((long) (Math.random() * 5000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                singleSubscriber.onSuccess(url);
            }));
        }

        return observables;
    }

    @Override
    public int getItemCount() {
        return isEmpty() ? 1 : genres.size();
    }

    private boolean isEmpty() {
        return genres.size() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty() && position == 0) return PLACEHOLDER;
        return GENRE;
    }

    public static class GenreHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_image)
        ImageView image;
        @BindView(R.id.item_genres)
        TextView genre;

        public GenreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class PlaceHolder extends RecyclerView.ViewHolder {

        public PlaceHolder(View itemView) {
            super(itemView);
        }
    }
}
