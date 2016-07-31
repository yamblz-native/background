package ru.yandex.yamblz.data;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleien on 31.07.16.
 */

public class Genre {
    private String name;
    @Nullable
    private List<Artist> artists;
    @Nullable
    private Uri cachedImage;

    public Genre(String name, List<Artist> artists) {
        this.name = name;
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public List<String> getCollageUrls() {
        List<String> urls = new ArrayList<>();
        for (Artist artist : artists) {
            urls.add(artist.cover.small);
            if (urls.size() >= 4) return urls;
        }

        return urls;
    }

    @Nullable
    public Uri getImage() {
        return cachedImage;
    }

    public void saveImage(@Nullable Uri cachedImage) {
        this.cachedImage = cachedImage;
    }
}
