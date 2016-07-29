package ru.yandex.yamblz.genre.data.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (!id.equals(artist.id)) return false;
        if (!name.equals(artist.name)) return false;
        return cover.equals(artist.cover);

    }

    @Override
    public int hashCode()
    {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + cover.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Artist{");
        builder.append("id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", genres=");
        builder.append(Arrays.toString(genres));
        builder.append(", cover=");
        builder.append(cover);
        builder.append("}");

        return builder.toString();
    }
}
