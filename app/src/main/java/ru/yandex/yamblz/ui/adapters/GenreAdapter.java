package ru.yandex.yamblz.ui.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.model.Genre;


public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreHolder> {


    protected boolean mIsLoading = false;

    // Store a member variable for the contacts
    private List<Genre> genres;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define listener member variable
    private static OnItemClickListener errorListener;
    private static OnItemClickListener itemListener;
    // Define the listener interface


    // Pass in the contact array into the constructor
    public GenreAdapter(List<Genre> genres) {
        this.genres = genres;
    }


    @Override
    public int getItemCount() {
        return !(mIsLoading) ? genres.size() : (genres.size() + 1);
    }


    @Override
    public void onBindViewHolder(GenreHolder holder, int position) {
        holder.photoView.setImageDrawable(null);
        holder.titleView.setText(genres.get(position).getName());
        holder.descView.setText(genres.get(position).getArtistString());
        CollageLoaderManager.getLoader()
                .loadCollage(genres.get(position).getArtistsPhotos(), holder.photoView);
    }


    @Override
    public GenreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GenreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.genres_list_item, parent, false));
    }


    public static class GenreHolder extends RecyclerView.ViewHolder {
        ImageView photoView;
        TextView titleView;
        TextView descView;

        public GenreHolder(View itemView) {
            super(itemView);
            photoView = (ImageView) itemView.findViewById(R.id.item_image);
            titleView = (TextView) itemView.findViewById(R.id.item_title);
            descView = (TextView) itemView.findViewById(R.id.item_desc);
        }
    }

}
