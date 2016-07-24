package ru.yandex.yamblz.artists.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ru.yandex.yamblz.artists.ArtistModel;

//Singleton для получения данных об артистах из любого места приложения

public class DataSingleton {
    private static final String TAG="DataSingleton";
    private static final String ARTIST_JSON_KEY = "cachedArtists";
    private static DataSingleton dataSingleton;
    private Context context;
    private List<ArtistModel> artists;
    private HashMap<String,List<ArtistModel>> artistsByGenre;

    private DataSingleton(Context context){
        this.context=context;
        String jsonString=CacheHelper.readCacheString(context, ARTIST_JSON_KEY);
        if(jsonString!=null){
            artists=parseData(jsonString);
            Log.i(TAG,"get data from cache");
        }
    }

    private List<ArtistModel> parseData(String json){
        CacheHelper.cacheString(context, ARTIST_JSON_KEY,json);
        Log.i(TAG, "save artists to cache");

        try {
            List<ArtistModel> artist = new ArrayList<>();
            JSONArray artistList = new JSONArray(json);
            artistsByGenre=new HashMap<>();
            for (int i = 0; i < artistList.length(); i++) {
                ArtistModel artistModel=new ArtistModel(artistList.getJSONObject(i));
                artist.add(artistModel);
                for(String genre:artistModel.genres){
                   List<ArtistModel> artistGenreList;
                    if(artistsByGenre.containsKey(genre)){
                        artistGenreList=artistsByGenre.get(genre);
                    }else{
                        artistGenreList=new ArrayList<>();
                        artistsByGenre.put(genre,artistGenreList);
                    }
                    artistGenreList.add(artistModel);
                }
            }
            Collections.sort(artist, (lhs, rhs) -> lhs.name.compareTo(rhs.name));
            return artist;

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("bad json data");
        }
    }

    public static void init(Context context){
        if(dataSingleton!=null){
            throw new RuntimeException("singleton must be init once");
        }else{
            dataSingleton=new DataSingleton(context);
        }
    }

    public void setData(String json){
        artists=parseData(json);
    }

    public boolean hasData(){
        return  false && artists!=null;
    }

    public List<ArtistModel> getArtists() {
        return artists;
    }

    public static DataSingleton get(){
        return dataSingleton;
    }

    public static void dispose(){
        dataSingleton=null;
    }

    public List<String> getGenres() {
        return new ArrayList<>(artistsByGenre.keySet());
    }

    public List<ArtistModel> getArtistsByGenre(String genre){
        return artistsByGenre.get(genre);
    }
}
