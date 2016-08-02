package ru.yandex.yamblz.ui.adapters;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.model.Artist;
import rx.Observable;
import rx.Subscription;

import static java.util.Collections.shuffle;
import static ru.yandex.yamblz.utils.StringUtils.getGenres;


public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenreViewHolder> {
    private static final String TAG = "GenresAdapter";
    private CollageLoader loader;
    private CriticalSectionsHandler criticalSectionsHandler;
    private HashMap<ImageView, Task> tasksMap = new HashMap<>();
    private HashMap<ImageView, Subscription> subscriptionsMap = new HashMap<>();
    private List<Artist> artists;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public GenresAdapter(List<Artist> artists) {
        this.artists = artists;
        loader = CollageLoaderManager.getLoader();
        criticalSectionsHandler = CriticalSectionsManager.getHandler();
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_genre, parent, false);
        return new GenreViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        Artist currentArtist = artists.get(position);
        List<String> genres = currentArtist.getGenres();

        holder.tvArtistName.setText(currentArtist.getName());
        holder.tvGenres.setText(getGenres(genres));
        holder.ivGenreCollage.setImageBitmap(null);
        holder.pbLoading.setVisibility(View.GONE);

        unsubscribe(holder.ivGenreCollage);

        Observable.from(artists)
                .filter(artist -> filterGenres(artist, genres))
                .map(artist1 -> artist1.getCover().getSmall())
                .toList()
                .map(this::toShuffledSubList)
                .subscribe(it -> startDownload(it, holder),
                        Throwable::printStackTrace);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }


    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGenreCollage;
        TextView tvArtistName;
        TextView tvGenres;
        ProgressBar pbLoading;

        public GenreViewHolder(View itemView) {
            super(itemView);
            ivGenreCollage = (ImageView) itemView.findViewById(R.id.ivGenreCollage);
            tvArtistName = (TextView) itemView.findViewById(R.id.tvArtistName);
            tvGenres = (TextView) itemView.findViewById(R.id.tvGenres);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pbLoading);
        }
    }

    private List<String> toShuffledSubList(List<String> links) {
        shuffle(links);
        int linksCount = links.size();
        int lastIndexForSublist;
        if (linksCount >= 9) {
            lastIndexForSublist = 9;
        } else if (linksCount >= 4) {
            lastIndexForSublist = 4;
        } else {
            lastIndexForSublist = 1;
        }
        return links.subList(0, lastIndexForSublist);
    }

    private void startDownload(List<String> links, GenreViewHolder holder) {
        ImageView targetView = holder.ivGenreCollage;
        Task task = () -> {
            holder.pbLoading.setVisibility(View.VISIBLE);
            Subscription subscription =
                    loader.loadCollage(links)
                            .subscribe(bitmap -> showBitmapOnHolder(bitmap, holder));
            subscriptionsMap.put(targetView, subscription);
        };
        tasksMap.put(targetView, task);
        criticalSectionsHandler.postLowPriorityTask(task);
    }

    private void showBitmapOnHolder(Bitmap bitmap, GenreViewHolder holder) {
        mainHandler.post(() -> {
            holder.pbLoading.setVisibility(View.INVISIBLE);
            holder.ivGenreCollage.setImageBitmap(bitmap);

            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(500);
            holder.ivGenreCollage.setAnimation(animation);
            animation.start();
        });
    }

    private boolean filterGenres(Artist artist, List<String> targetGenres) {
        for (int i = 0, size = targetGenres.size(); i < size; i++) {
            if (artist.getGenres().contains(targetGenres.get(i))) {
                return true;
            }
        }
        return false;
    }

    private void unsubscribe(ImageView view) {
        if (tasksMap.containsKey(view)) {
            criticalSectionsHandler.removeLowPriorityTask(tasksMap.get(view));
            tasksMap.remove(view);
        }
        if (subscriptionsMap.containsKey(view)) {
            Subscription subscription = subscriptionsMap.get(view);
            if (subscription != null) {
                subscription.unsubscribe();
            }
        }
    }

}
