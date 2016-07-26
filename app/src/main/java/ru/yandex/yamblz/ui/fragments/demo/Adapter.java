package ru.yandex.yamblz.ui.fragments.demo;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Artist;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Pair<List<Artist>, String>> artistByGenres = Collections.emptyList();

    void setArtistByGenres(List<Pair<List<Artist>, String>> artistByGenres) {
        this.artistByGenres = artistByGenres;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(artistByGenres.get(position).second);

        holder.subscription = Observable.from(artistByGenres.get(position).first)
                .map(artist -> artist.smallCover)
                .toList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Adapter.class.getSimpleName(), "", e);
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        holder.subscription =
                                CollageLoaderManager.getLoader()
                                        .loadCollage(strings, holder.imageView);
                    }
                });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.imageView.setImageResource(android.R.color.transparent);
        holder.subscription.unsubscribe();
    }

    @Override
    public int getItemCount() {
        return artistByGenres.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_collage)
        ImageView imageView;
        @BindView(R.id.item_name)
        TextView textView;

        Subscription subscription;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
