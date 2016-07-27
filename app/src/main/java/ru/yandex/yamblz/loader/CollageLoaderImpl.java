package ru.yandex.yamblz.loader;

import android.widget.ImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.yandex.yamblz.model.Artist;
import ru.yandex.yamblz.model.ArtistFetcher;


public class CollageLoaderImpl implements CollageLoader {
    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {

    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {

    }

    public void doSomething() throws IOException {
        List<Artist> inputArtists = new ArtistFetcher().getArtistsFromJson();

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
    }
}