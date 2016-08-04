package ru.yandex.yamblz.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Cover;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Volha on 04.08.2016.
 */

public class GenresRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Map<String, ArrayList<Cover>> genres = new HashMap<>();
    List<String> keys;
    Map<View, Subscription> subscriptions = new HashMap<>();

    public GenresRecyclerAdapter() {
    }

    public void setItems(Map<String, ArrayList<Cover>> genres) {
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

            if (subscriptions.containsKey(genre.cover))
                subscriptions.get(genre.cover).unsubscribe();

            subscriptions.put(genre.cover, loadCovers(genre.cover, getItemByPosition(position)));
        }
    }

    private Subscription loadCovers(ImageView itemView, ArrayList<Cover> covers) {
        return rx.Observable
                .from(covers)
                .observeOn(Schedulers.io())
                .map(cover -> loadImage( cover.getSmall() ))
                .subscribeOn(Schedulers.computation())
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmaps -> {
                    itemView.setImageBitmap(makeCollage((ArrayList<Bitmap>) bitmaps));
                });
    }

    private Bitmap makeCollage(ArrayList<Bitmap> bitmaps) {
        if ( bitmaps.size() < 4)
            return bitmaps.get(0);

        int columns = 2;
        int rows = 2;

        int itemWidth = bitmaps.get(0).getWidth();
        int width = itemWidth * columns;
        int itemHeight = bitmaps.get(0).getHeight();
        int height = itemHeight * rows;

        Bitmap collage = Bitmap.createBitmap(width, height, bitmaps.get(0).getConfig());
        Canvas canvas = new Canvas(collage);
        for ( int i = 0; i < rows; ++i ) {
            for (int j = 0; j < columns; j++) {
                Bitmap bitmap = bitmaps.get( i * rows + j );
                canvas.drawBitmap( bitmap, j * itemWidth, i * itemHeight, null );
            }
        }

        return collage;
    }

    private Bitmap loadImage(String url) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        return imageLoader.loadImageSync(url, new ImageSize(100, 100));
    }

    public ArrayList<Cover> getItemByPosition(int position) {
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

        public GenreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
