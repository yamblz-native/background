package ru.yandex.yamblz.genre.interfaces;

import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Genre;

/**
 * Created by platon on 26.07.2016.
 */
public interface GenresView {
    void showProgress(boolean show);
    void showGenres(List<Genre> genres);
    void showError(String error);
}
