package ru.yandex.yamblz.genre.data.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by platon on 26.07.2016.
 */
public class Cover implements Serializable
{
    @SerializedName("small")
    private String coverUrl;

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }
}
