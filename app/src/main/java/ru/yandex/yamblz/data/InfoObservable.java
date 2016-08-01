package ru.yandex.yamblz.data;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class InfoObservable {

    private static final String jsonURL = "http://cache-spb03.cdn.yandex.net/" +
            "download.cdn.yandex.net/mobilization-2016/artists.json";
    private static final String TAG = InfoObservable.class.getSimpleName();

    public static Observable<List<Artist>> getObservable() {
        return Observable.fromCallable(InfoObservable::download);
    }

    private static List<Artist> download() throws IOException {
        try (JsonReader reader = new JsonReader(
                new InputStreamReader(
                        new URL(jsonURL)
                                .openStream(),
                        "UTF-8"))) {
            Log.d(TAG, "downloading");
            List<Artist> artists = new ArrayList<>();
            reader.beginArray();

            while (reader.hasNext()) {
                Artist artist = new Artist();
                reader.beginObject();

                while (reader.hasNext()) {
                    String name = reader.nextName();
                    switch (name) {
                        case "id":
                            artist.id = reader.nextInt();
                            break;
                        case "name":
                            artist.name = reader.nextString();
                            break;
                        case "genres":
                            List<String> genres = new ArrayList<>();
                            reader.beginArray();
                            while (reader.hasNext()) {
                                genres.add(reader.nextString());
                            }
                            reader.endArray();
                            artist.genres = genres;
                            break;
                        case "tracks":
                            artist.tracks = reader.nextInt();
                            break;
                        case "albums":
                            artist.albums = reader.nextInt();
                            break;
                        case "link":
                            artist.link = reader.nextString();
                            break;
                        case "description":
                            artist.description = reader.nextString();
                            break;
                        case "cover":
                            reader.beginObject();
                            for (int i = 0; i < 2; ++i) {
                                String size = reader.nextName();
                                String cover = reader.nextString();
                                switch (size) {
                                    case "small":
                                        artist.smallCover = cover;
                                        break;
                                    case "big":
                                        artist.bigCover = cover;
                                        break;
                                    default:
                                        Log.d(TAG, "Unknown cover size " + size);
                                        break;
                                }
                            }
                            reader.endObject();
                            break;
                        default:
                            Log.w(TAG, "Unknown name '" + name + "'");
                            reader.skipValue();
                            break;
                    }
                }

                reader.endObject();
                artists.add(artist);
            }

            reader.endArray();

            Log.d(TAG, "downloaded");

            return artists;
        }
    }

}
