package ru.yandex.yamblz.ui.adapters;

import android.widget.ImageView;

import ru.yandex.yamblz.data.Genre;
import ru.yandex.yamblz.handler.Task;

/**
 * Created by dalexiv on 8/8/16.
 */
public interface IRetrieveGenreImage {
    void postDownloadingTask(Genre genre, ImageView toPost);
    void removeOldTask(Task task);
}
