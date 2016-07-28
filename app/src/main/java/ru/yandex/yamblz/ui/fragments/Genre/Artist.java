package ru.yandex.yamblz.ui.fragments.Genre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kostya on 26.07.16.
 */
public class Artist {
    private static final String DEFAULT_STR = "unknown";
    private static final String DEFAULT_URL = "";

    private String name;
    private String imgUrl;
    private List<String> genres = new ArrayList<>();

    public List<String> getGenres() {
        return genres;
    }

    public Artist(JSONObject jsonObject) {
        try { this.name   = jsonObject.getString("name"); }                         catch (JSONException e) { this.name   = DEFAULT_STR; }
        try { this.imgUrl = jsonObject.getJSONObject("cover").getString("small"); } catch (JSONException e) { this.imgUrl = DEFAULT_URL; }
        try {
            JSONArray jsonGenres = jsonObject.getJSONArray("genres");
            for (int i = 0; i < jsonGenres.length(); i++) {
                genres.add((String) jsonGenres.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Artist> fromJSON (JSONArray jsonArray) {
        ArrayList<Artist> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(new Artist(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
