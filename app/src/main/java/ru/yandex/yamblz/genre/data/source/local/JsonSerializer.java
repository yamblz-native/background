package ru.yandex.yamblz.genre.data.source.local;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Artist;

/**
 * Created by platon on 10.08.2016.
 */
public class JsonSerializer
{
    private final Gson gson = new Gson();

    public String serialize(List<Artist> artist)
    {
        return gson.toJson(artist);
    }

    public List<Artist> deserialize(String jsonString)
    {
        return gson.fromJson(jsonString, new TypeToken<List<Artist>>() {}.getType());
    }
}
