package ru.yandex.yamblz.ui.adapters;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.artists.utils.DataSingleton;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.DefaultImageTarget;
import ru.yandex.yamblz.loader.ImageTarget;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.MyViewHolder> {
    private List<String> genres;

    public GenresAdapter(List<String> genres) {
        this.genres = genres;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(genres.get(position));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView collage;
        TextView textView;
        ImageTarget imageTarget;
        private String prevGenre=null;
        MyViewHolder(View itemView) {
            super(itemView);
            collage= (ImageView) itemView.findViewById(R.id.image_collage);
            textView= (TextView) itemView.findViewById(R.id.title);
            imageTarget=new DefaultImageTarget(collage);
        }

        void bind(String genre){
            if(prevGenre!=null){
                CollageLoaderManager.getLoader().cancel(prevGenre);
            }
            prevGenre=genre;
            collage.setImageDrawable(new ColorDrawable(Color.BLACK));
            textView.setText(genre);
            CollageLoaderManager.getLoader().loadCollage(DataSingleton.get().getImagesForGenre(genre),imageTarget,genre);
        }
    }
}
