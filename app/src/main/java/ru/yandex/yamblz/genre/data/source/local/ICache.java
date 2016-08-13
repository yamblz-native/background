package ru.yandex.yamblz.genre.data.source.local;

import java.util.List;

/**
 * Created by platon on 19.07.2016.
 */
public interface ICache<T>
{
    List<T> get();
    void put(List<T> list);
    boolean clear();
    boolean isEmpty();
}
