package ru.yandex.yamblz.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArtistFetcher {
    private static final String JSON_URL = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    public String getJson() throws IOException {
        return getJsonFromUrl(JSON_URL);
    }

    public List<Artist> getArtistsFromJson() throws IOException {
        return getArtistsFromJson(getJson());
    }

    public List<Artist> getArtistsFromJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Artist>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    private String getJsonFromUrl(String urlSpec) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlSpec)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}