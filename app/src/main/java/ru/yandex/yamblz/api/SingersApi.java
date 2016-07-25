package ru.yandex.yamblz.api;

import android.support.annotation.Nullable;

import java.util.List;

import ru.yandex.yamblz.models.Singer;

public interface SingersApi {

    /**
     * Returns list of available singers
     * @return list of singers or {@code null} if an error occured
     */
    @Nullable List<Singer> getSingers();

}
