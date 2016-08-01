package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import ru.yandex.yamblz.R;

/**
 * Adapter for RecycleView
 */
abstract class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.GroupsViewHolder> {


    class GroupsViewHolder extends RecyclerView.ViewHolder {
        WeakReference<TextView>  genre;
        WeakReference<ImageView> cover;

        GroupsViewHolder(View itemView) {
            super(itemView);
            genre = new WeakReference<>((TextView) itemView.findViewById(R.id.genre));
            cover = new WeakReference<>((ImageView) itemView.findViewById(R.id.cover));
        }
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new GroupsViewHolder(view);
    }


}
