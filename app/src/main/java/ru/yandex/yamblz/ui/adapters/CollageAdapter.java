package ru.yandex.yamblz.ui.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.adapters.CollageAdapter.CollageHolder;

public class CollageAdapter extends Adapter<CollageHolder> {
    private String[] dataSet;

    static class CollageHolder extends ViewHolder {
        TextView textView;

        public CollageHolder(TextView view) {
            super(view);
            textView = view;
        }
    }

    public CollageAdapter(String[] dataSet) {
        this.dataSet = dataSet;
    }


    @Override
    public CollageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false);
        return new CollageHolder(textView);
    }


    @Override
    public void onBindViewHolder(CollageHolder holder, int position) {
        holder.textView.setText(dataSet[position]);

    }


    @Override
    public int getItemCount() {
        return dataSet.length;
    }
}
