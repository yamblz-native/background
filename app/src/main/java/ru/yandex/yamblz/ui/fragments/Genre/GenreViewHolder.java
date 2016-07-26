package ru.yandex.yamblz.ui.fragments.Genre;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import ru.yandex.yamblz.R;

/**
 * Created by kostya on 26.07.16.
 */
public class GenreViewHolder extends RecyclerView.ViewHolder {

    ImageView imgInList;
    TextView textInList;

    public GenreViewHolder(View itemView) {
        super(itemView);
        imgInList = (ImageView) itemView.findViewById(R.id.img_in_list);
        textInList = (TextView) itemView.findViewById(R.id.text_in_list);
    }


}
