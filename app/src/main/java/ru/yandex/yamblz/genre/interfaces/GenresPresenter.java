package ru.yandex.yamblz.genre.interfaces;

/**
 * Created by platon on 26.07.2016.
 */
public interface GenresPresenter<T> {
    void getGenres(boolean forceLoad);
    void bind(T view);
    void unbind();
    void unsubscribe();
}
