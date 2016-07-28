package ru.yandex.yamblz.ui.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageStrategyImpl;
import ru.yandex.yamblz.ui.fragments.Genre.Artist;
import ru.yandex.yamblz.ui.fragments.Genre.DownloadArtistsTask;
import ru.yandex.yamblz.ui.fragments.Genre.Genre;
import ru.yandex.yamblz.ui.fragments.Genre.GenreAdapter;
import ru.yandex.yamblz.ui.fragments.Genre.GenreOnScrollListener;

public class ContentFragment extends BaseFragment {
    private static final String urlStr = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
    ImageView imageView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView  = (RecyclerView)inflater.inflate(R.layout.fragment_content, container, false);
        List<Genre> genres = getGenres();
        RecyclerView.Adapter adapter = new GenreAdapter(genres, super.getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new GenreOnScrollListener());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    private List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        List<Artist> artists = null;
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("genres", 0);
        String artistsJSON = sharedPreferences.getString("artistsJSON", null);
        if (artistsJSON == null) {
            Log.d("Main list fragment", "download");
            DownloadArtistsTask downloadArtists = new DownloadArtistsTask();
            downloadArtists.execute(urlStr);
            try {
                artistsJSON = downloadArtists.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("artistsJSON", artistsJSON);
            editor.apply();
        }
        try {
            artists = Artist.fromJSON(new JSONArray(artistsJSON));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Genre> genreMap = new HashMap<>();

        assert artists != null;
        for (Artist artist: artists) {
            for(String genre: artist.getGenres()) {
                if (!genreMap.containsKey(genre)) {
                    Genre genreObj = new Genre(genre);
                    genreMap.put(genre, genreObj);
                }
                genreMap.get(genre).addArtist(artist);
            }
        }

        genres.addAll(genreMap.values());

        return genres;
    }
}
