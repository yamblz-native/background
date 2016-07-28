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
}
