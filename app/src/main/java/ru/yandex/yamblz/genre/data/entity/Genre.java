package ru.yandex.yamblz.genre.data.entity;

import java.util.List;

/**
 * Created by platon on 26.07.2016.
 */
public class Genre
{
    private List<String> urls;
    private String name;

    public List<String> getUrls()
    {
        return urls;
    }

    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genre genre = (Genre) o;

        if (!urls.equals(genre.urls)) return false;
        return name.equals(genre.name);

    }

    @Override
    public int hashCode()
    {
        int result = urls.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Genre{");
        builder.append("urls=");
        builder.append(urls);
        builder.append(", name=");
        builder.append(name);
        builder.append("}");

        return builder.toString();
    }
}
