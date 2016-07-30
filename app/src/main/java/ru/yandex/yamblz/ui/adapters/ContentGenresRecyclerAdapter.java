package ru.yandex.yamblz.ui.adapters;

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
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageLoaderImpl;
import ru.yandex.yamblz.model.Genre;

public class ContentGenresRecyclerAdapter extends RecyclerView.Adapter<ContentGenresRecyclerAdapter.GenreHolder> {
    private List<Genre> mGenreList;
    private LayoutInflater mLayoutInflater;

    public ContentGenresRecyclerAdapter(List<Genre> genreList, LayoutInflater layoutInflater) {
        mGenreList = genreList;
        mLayoutInflater = layoutInflater;
    }

    @Override
    public GenreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_genre_item, parent, false);
        return new GenreHolder(view, mGenreList);
    }

    @Override
    public void onBindViewHolder(GenreHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mGenreList.size();
    }

    public class GenreHolder extends RecyclerView.ViewHolder {
        List<Genre> mGenreList;
        CollageLoader mCollageLoader = new CollageLoaderImpl();

        @BindView(R.id.list_genre_item_image)
        ImageView mCollage;

        @BindView(R.id.list_genre_item_text)
        TextView mText;

        public GenreHolder(View itemView, List<Genre> genreList) {
            super(itemView);
            mGenreList = genreList;
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            mCollageLoader.loadCollage(mGenreList.get(position).getSmallCoverUrlList(), mCollage);
        }
    }
}
