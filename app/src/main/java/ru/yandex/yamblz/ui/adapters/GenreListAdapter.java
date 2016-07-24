package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.FourImagesCollageStrategy;
import ru.yandex.yamblz.model.Genre;

/**
 * Created by Aleksandra on 25/07/16.
 */
public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.GenreViewHolder> {
    public static final String DEBUG_TAG = GenreListAdapter.class.getName();
    private List<Genre> dataset;

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_genre, parent, false);
        return new GenreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        if (position < dataset.size()) {
            Genre genre = dataset.get(position);
            holder.name.setText(genre.getName());
            Log.d(DEBUG_TAG, Arrays.deepToString(genre.getUrls().toArray()));

            new Thread(() -> {
                CollageLoaderManager.getLoader().loadCollage(genre.getUrls(), holder.image, new FourImagesCollageStrategy());
            }).start();
        }
    }

    @Override
    public int getItemCount() {
        return dataset != null ? dataset.size() : 0;
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_genre)
        TextView name;

        @BindView(R.id.image_view_genre)
        ImageView image;

        public GenreViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public void setDataset(List<Genre> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }
}
