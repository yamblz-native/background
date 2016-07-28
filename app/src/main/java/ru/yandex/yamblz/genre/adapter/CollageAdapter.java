package ru.yandex.yamblz.genre.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.interfaces.CollageLoader;
import ru.yandex.yamblz.loader.interfaces.ImageTarget;
import ru.yandex.yamblz.loader.ImageTargetImpl;
import ru.yandex.yamblz.genre.data.entity.Genre;

/**
 * Created by platon on 26.07.2016.
 */
public class CollageAdapter extends RecyclerView.Adapter<CollageAdapter.CollageItemHolder>
{
    private CollageLoader collageLoader;
    private List<Genre> genres;

    public CollageAdapter(CollageLoader loader)
    {
        collageLoader = loader;
    }

    @Override
    public CollageItemHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return new CollageItemHolder(v);
    }

    @Override
    public void onBindViewHolder(CollageItemHolder holder, int position)
    {
        Genre genre = genres.get(position);
        holder.bind(genre);
    }

    @Override
    public int getItemCount()
    {
        return genres == null ? 0 : genres.size();
    }

    public void swap(List<Genre> newList)
    {
        genres = newList;
        notifyDataSetChanged();
    }

    public void clear()
    {
        genres.clear();
    }

    public class CollageItemHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.iv_collage_container) ImageView collageContainer;
        @BindView(R.id.tw_genre) TextView genreTextView;
        ImageTarget imageTarget = null;

        public CollageItemHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(Genre genre)
        {
            collageContainer.setImageResource(R.drawable.ic_place_holder);
            genreTextView.setText(genre.getName());

            if (imageTarget != null) imageTarget.clear();

            imageTarget = new ImageTargetImpl(collageContainer);
            collageLoader.loadCollage(genre.getUrls(), imageTarget);
        }
    }
}
