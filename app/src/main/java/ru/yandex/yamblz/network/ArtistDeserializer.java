package ru.yandex.yamblz.network;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import ru.yandex.yamblz.model.Artist;

public class ArtistDeserializer implements JsonDeserializer<Artist> {

    @Override
    public Artist deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonArtist = json.getAsJsonObject();
        JsonObject jsonCover = jsonArtist.get(ApiConstants.PARAMETER_COVER).getAsJsonObject();

        Artist artist = new Artist();

        artist.setId(jsonArtist.get(ApiConstants.PARAMETER_ID).getAsLong());
        artist.setName(jsonArtist.get(ApiConstants.PARAMETER_NAME).getAsString());
        artist.setGenres(new Gson().fromJson(jsonArtist.get(ApiConstants.PARAMETER_GENRES).getAsJsonArray(), String[].class));
        artist.setTracks(jsonArtist.get(ApiConstants.PARAMETER_TRACKS).getAsInt());
        artist.setAlbum(jsonArtist.get(ApiConstants.PARAMETER_ALBUMS).getAsInt());
        if (jsonArtist.has(ApiConstants.PARAMETER_LINK)) {
            artist.setLink(jsonArtist.get(ApiConstants.PARAMETER_LINK).getAsString());
        }
        artist.setDescription(jsonArtist.get(ApiConstants.PARAMETER_DESCRIPTION).getAsString());
        artist.setSmallCoverUrl(jsonCover.get(ApiConstants.PARAMETER_COVER_SMALL).getAsString());
        artist.setBigCoverUrl(jsonCover.get(ApiConstants.PARAMETER_COVER_BIG).getAsString());

        return artist;
    }

}
