package ru.yandex.yamblz.genre.data.source;

import java.util.List;

public interface Cache<T>
{
    List<T> get();
    void put(List<T> list);
    boolean clear();
    boolean isEmpty();
}
