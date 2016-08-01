package ru.yandex.yamblz.ui.adapters;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.model.Singer;
import solid.stream.Stream;

public class FirstRecyclerAdapter extends RecyclerAdapter {

    private List<String> genres_list;
    private CollageLoader collageLoader;
    private Map<String, List<String>> urls; // genre -> list of urls

    public FirstRecyclerAdapter(Map<String, Stream<Singer>> s, CollageLoader loader) {
        Log.w("Adapter", "constructor");
        collageLoader = loader;
        urls = new HashMap<>();
        setSingers(s);
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        Log.w("Adapter", "onBind");
        holder.genre.get().setText(genres_list.get(position));
        holder.cover.get().setImageBitmap(null);
        collageLoader.loadCollage(urls.get(genres_list.get(position)), holder.cover.get());
    }

    public void setSingers(Map<String, Stream<Singer>> genres) {
        Log.w("Adapter", "setSingers");
        if (genres == null) return;
        genres_list = new ArrayList<>(genres.keySet());
        for (String genre: genres_list) {
            List<String> url = new ArrayList<>();
            Stream<Singer> stream = genres.get(genre);
            stream.forEach(singer -> url.add(singer.getCover_small()));
            urls.put(genre, url);
        }
    }

    @Override
    public int getItemCount() {
        if (urls == null) return 0;
        return urls.size();
    }

}
