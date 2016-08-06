package ru.yandex.yamblz.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.model.Artist;
import ru.yandex.yamblz.model.Genre;
import ru.yandex.yamblz.network.ApiConstants;
import ru.yandex.yamblz.network.ArtistDeserializer;
import ru.yandex.yamblz.network.YandexService;
import ru.yandex.yamblz.ui.adapters.GenresListAdapter;
import ru.yandex.yamblz.utils.GenresScrollListener;

public class ContentFragment extends BaseFragment {

    YandexService service;

    ArrayList<Genre> genres;
    RecyclerView rvGenres;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        initViews(view);
        initService();
        new ArtistsLoadTask().execute();

        return view;
    }

    private void initViews(View view) {
        rvGenres = (RecyclerView) view.findViewById(R.id.rv_genres);
        rvGenres.setOnScrollListener(new GenresScrollListener());
    }

    private void initService() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.registerTypeAdapter(Artist.class, new ArtistDeserializer()).create();

        service = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(YandexService.class);
    }

    class ArtistsLoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Artist> artists = new ArrayList<>();

            try {
                artists = service.getArtists().execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String, Genre> genresMap = new HashMap<>();
            for (Artist artist : artists) {
                for (String genreName : artist.getGenres()) {
                    if (genresMap.containsKey(genreName)) {
                        genresMap.get(genreName).getUrls().add(artist.getSmallCoverUrl());
                    } else {
                        Genre genre = new Genre();
                        genre.setName(genreName);
                        genre.getUrls().add(artist.getSmallCoverUrl());
                        genresMap.put(genreName, genre);
                    }
                }
            }

            genres = new ArrayList<>(genresMap.size());

            for (Map.Entry<String, Genre> entry : genresMap.entrySet()) {
                genres.add(entry.getValue());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            GenresListAdapter adapter = new GenresListAdapter(genres, CollageLoaderManager.getLoader());
            rvGenres.setAdapter(adapter);
        }

    }

}
