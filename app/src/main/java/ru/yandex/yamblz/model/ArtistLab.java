package ru.yandex.yamblz.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArtistLab {
    private static final String TAG = "ArtistLab";
    private static final String ARTIST_LIST_NAME = "artistList";


    @SuppressLint("StaticFieldLeak")
    private static ArtistLab sArtistLab;

    private Context mContext;
    private List<Artist> mArtists;

    private ArtistLab(Context context) {
        mContext = context.getApplicationContext();
        mArtists = new ArrayList<>();
    }

    public static ArtistLab get(Context context) {
        if (sArtistLab == null) {
            sArtistLab = new ArtistLab(context);
        }
        return sArtistLab;
    }

    public List<Artist> getArtists() {
        shouldLoadArtist();
        return mArtists;
    }

    public void setArtists(List<Artist> artists) {
        mArtists = artists;
        saveArtists(mArtists);
    }

    public Artist getArtist(int position) {
        shouldLoadArtist();
        return mArtists.get(position);
    }

    public Map<String, Set<Artist>> getGenresMap() {
        shouldLoadArtist();
        return artistsToGenres(mArtists);
    }

    public List<Genre> getGenresList() {
        shouldLoadArtist();
        return genresMapToList(artistsToGenres(mArtists));
    }

    private void shouldLoadArtist() {
        List<Artist> tempArtists = loadArtists();

        if (tempArtists != null) {
            mArtists = tempArtists;
        } else {
            mArtists = new ArrayList<>();
        }
    }

    private Map<String, Set<Artist>> artistsToGenres(List<Artist> inputArtists) {
        Map<String, Set<Artist>> outputGenres = new HashMap<>();

        for (Artist currentArtist : inputArtists) {
            for (String currentGenre : currentArtist.getGenresSet()) {
                if (outputGenres.containsKey(currentGenre)) {
                    outputGenres.get(currentGenre).add(currentArtist);
                } else {
                    Set<Artist> newArtistSet = new HashSet<>();
                    newArtistSet.add(currentArtist);
                    outputGenres.put(currentGenre, newArtistSet);
                }
            }
        }

        return outputGenres;
    }

    private List<Genre> genresMapToList(Map<String, Set<Artist>> genresMap) {
        List<Genre> genreList = new ArrayList<>();

        for (Map.Entry<String, Set<Artist>> mapEntry : genresMap.entrySet()) {
            Genre genre = new Genre(mapEntry.getKey());

            for (Artist artist : mapEntry.getValue()) {
                genre.addArtist(artist);
            }

            genreList.add(genre);
        }

        Collections.sort(genreList);

        return genreList;
    }

    // В БД нет смысла, слишком простой случай
    public void saveArtists(List<Artist> artists) {
        try {
            FileOutputStream fos = mContext.openFileOutput(ARTIST_LIST_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(artists);
            oos.close();
        } catch (IOException ioe) {
            Log.w(TAG, "Can't save artists: " + ioe.getMessage());
        }
    }

    public List<Artist> loadArtists() {
        try {
            FileInputStream fis;
            fis = mContext.openFileInput(ARTIST_LIST_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<Artist> artists = (ArrayList<Artist>) ois.readObject();
            ois.close();
            return artists;
        } catch (Exception e) {
            Log.w(TAG, "Can't load artists: " + e.getMessage());
            File file = new File(mContext.getFilesDir().getAbsolutePath() + "/" + ARTIST_LIST_NAME);
            if (!file.delete()) {
                Log.e(TAG, "Can't delete file!");
            }
            return null;
        }
    }
}