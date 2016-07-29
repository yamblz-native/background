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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cover cover = (Cover) o;

        return coverUrl.equals(cover.coverUrl);

    }

    @Override
    public int hashCode()
    {
        return coverUrl.hashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Cover{");
        builder.append("coverUrl=");
        builder.append(coverUrl);
        builder.append("}");

        return builder.toString();
    }
}
