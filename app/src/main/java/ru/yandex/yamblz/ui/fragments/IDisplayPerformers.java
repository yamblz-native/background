package ru.yandex.yamblz.ui.fragments;


import ru.yandex.yamblz.data.Genre;
import rx.Observable;

/**
 * Created by dalexiv on 8/8/16.
 */

public interface IDisplayPerformers {
    void setRefreshing(boolean isRefreshing);
    void notifyUser(String message);
    void addGenre(Genre genre);
    void clearPerformers();
}
