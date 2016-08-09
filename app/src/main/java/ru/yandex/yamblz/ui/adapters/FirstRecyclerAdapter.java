package ru.yandex.yamblz.ui.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.model.Singer;
import solid.stream.Stream;

public class FirstRecyclerAdapter extends RecyclerAdapter {

    private List<String> genres_list;
    private CollageLoader collageLoader;
    private Map<String, List<String>> urls; // genre -> list of urls
    private CriticalSectionsHandler sectionsHandler;
    private Map<RecyclerView.ViewHolder, Task> startedDownloads;

    public FirstRecyclerAdapter(Map<String, Stream<Singer>> s, CollageLoader loader) {
        Log.w("Adapter", "constructor");
        collageLoader = loader;
        sectionsHandler = CriticalSectionsManager.getHandler();
        urls = new HashMap<>();
        startedDownloads = new HashMap<>();
        setSingers(s);
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        holder.genre.get().setText(genres_list.get(position));
        holder.cover.get().setImageBitmap(null);
        Task old = startedDownloads.get(holder);
        if (old != null) {
            sectionsHandler.removeLowPriorityTask(old);
        }
        startedDownloads.remove(holder);
        Task newTask = () -> holder.setSubscription(collageLoader.loadCollage(urls.get(genres_list.get(position)),
                holder.cover.get()));
        startedDownloads.put(holder, newTask);
        sectionsHandler.postLowPriorityTask(newTask);
    }

    @Override
    public void onViewRecycled(GroupsViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.subscription != null)
            holder.subscription.unsubscribe();
    }

    public void setSingers(Map<String, Stream<Singer>> genres) {
        if (genres == null) return;
        genres_list = new ArrayList<>(genres.keySet());
        for (String genre : genres_list) {
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
