package ru.yandex.yamblz.loader;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.yandex.yamblz.model.Singer;

/**
 * Created by vorona on 27.07.16.
 */

public class JsonLoad {
    public List<Singer> loadSingers() {
        List<Singer> singers;
        try {
            singers = getSinger();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return singers;
    }

    public List<Singer> getSinger() throws IOException {
        URL url = new URL("http://cache-spb05.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in = connection.getInputStream();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(in));
            List<Singer> res = readJson(reader);
            reader.close();
            Log.w("Loader", "finished loading" + res.get(0).getGenres());
            return res;
        } finally {
            if (in != null) {
                in.close();
            }
            connection.disconnect();
        }

    }

    /**
     * Combine all performers
     */
    private List<Singer> readJson(JsonReader reader) throws IOException {
        ArrayList<Singer> ar = new ArrayList<>();
        Singer cur;
        reader.beginArray();
        while (reader.hasNext()) {
            cur = takeData(reader);
            ar.add(cur);
        }
        reader.endArray();

        return ar;
    }

    /**
     * Read information about individual performers
     */
    private Singer takeData(JsonReader reader) throws IOException {
        Singer res = new Singer();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    res.setId(reader.nextLong());
                    break;
                case "name":
                    res.setName(reader.nextString());
                    break;
                case "tracks":
                    res.setTracks((int) reader.nextLong());
                    break;
                case "albums":
                    res.setAlbums((int) reader.nextLong());
                    break;
                case "link":
                    res.setLink(reader.nextString());
                    break;
                case "description":
                    res.setBio(reader.nextString());
                    break;
                case "cover":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String s = reader.nextName();
                        switch (s) {
                            case "small":
                                res.setCover_small(reader.nextString());
                                break;
                            case "big":
                                res.setCover_big(reader.nextString());
                                break;
                            default:
                                reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                case "genres":
                    reader.beginArray();
                    StringBuilder str = new StringBuilder();
                    while (reader.hasNext()) {
                        String s = reader.nextString();
                        str.append(s).append(", ");
                    }
                    reader.endArray();
                    if (str.lastIndexOf(", ") > 0 && str.length() > 0) {
                        res.setGenres(str.toString().substring(0, str.lastIndexOf(",")));
                    }
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return res;
    }

}
