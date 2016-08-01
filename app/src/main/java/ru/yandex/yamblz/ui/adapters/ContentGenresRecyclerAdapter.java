package ru.yandex.yamblz.ui.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import ru.yandex.yamblz.model.Genre;

public class ContentGenresRecyclerAdapter extends RecyclerView.Adapter<ContentGenresRecyclerAdapter.GenreHolder> {
    private List<Genre> mGenreList;
    private LayoutInflater mLayoutInflater;
    private Drawable mNoCollage;
    private CollageLoader mCollageLoader;

    public ContentGenresRecyclerAdapter(List<Genre> genreList, LayoutInflater layoutInflater, Drawable drawable, CollageLoader collageLoader) {
        mGenreList = genreList;
        mLayoutInflater = layoutInflater;
        mNoCollage = drawable;
        mCollageLoader = collageLoader;
    }

    @Override
    public GenreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_genre_item, parent, false);
        return new GenreHolder(view, mGenreList, mNoCollage);
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
        Drawable mNoCollage;

        @BindView(R.id.list_genre_item_image)
        ImageView mCollageImageView;

        @BindView(R.id.list_genre_item_text)
        TextView mText;

        public GenreHolder(View itemView, List<Genre> genreList, Drawable drawable) {
            super(itemView);
            mGenreList = genreList;
            mNoCollage = drawable;
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            mCollageImageView.setImageDrawable(mNoCollage);
            mCollageLoader.loadCollage(mGenreList.get(position).getSmallCoverUrlList(), mCollageImageView);
            mText.setText(mGenreList.get(position).getName());
            Log.d("BIND", "Hash=" + hashCode() + "; Pos=" + position + "; Genre=" + mGenreList.get(position).getName());
        }
    }
}