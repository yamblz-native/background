package ru.yandex.yamblz.genre.data.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by platon on 26.07.2016.
 */
public class Artist implements Serializable
{
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("genres")
    private String[] genres;

    @SerializedName("cover")
    private Cover cover;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String[] getGenres()
    {
        return genres;
    }

    public void setGenres(String[] genres)
    {
        this.genres = genres;
    }

    public Cover getCover()
    {
        return cover;
    }

    public void setCover(Cover cover)
    {
        this.cover = cover;
    }
}
