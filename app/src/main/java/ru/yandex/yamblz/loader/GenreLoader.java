package ru.yandex.yamblz.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.yandex.yamblz.Constants;
import ru.yandex.yamblz.model.ArtistInfo;
import ru.yandex.yamblz.model.Genre;

/**
 * Created by Aleksandra on 25/07/16.
 */
public class GenreLoader extends AsyncTaskLoader<List<Genre>> {
    public static final String DEBUG_TAG = GenreLoader.class.getName();

    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();
    private TypeToken<List<ArtistInfo>> artists = new TypeToken<List<ArtistInfo>>() {
    };

    public GenreLoader(Context context) {
        super(context);
    }

    @Override
    public List<Genre> loadInBackground() {
        Request request = new Request.Builder()
                .url(Constants.MOBILIZATION_URL)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            List<ArtistInfo> artistInfoList = gson.fromJson(responseBody.charStream(), artists.getType());
            responseBody.close();

            Map<String, Genre> genres = new HashMap<>();
            for (ArtistInfo a : artistInfoList) {
                for (String g : a.getGenres()) {
                    if (genres.containsKey(g)) {
                        genres.get(g).getUrls().add(a.getCover().getSmall());
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(a.getCover().getSmall());
                        genres.put(g, new Genre(list, g));
                    }
                }
            }
            List<Genre> result = new ArrayList<>(genres.size());

            for (Map.Entry<String, Genre> entry : genres.entrySet()) {
                result.add(entry.getValue());
            }

            for (Genre g : result) {
                Log.d(DEBUG_TAG, "" + g.toString());
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
