package ru.yandex.yamblz.data;

/**
 * Created by Volha on 01.08.2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder( {
        "id",
        "name",
        "genres",
        "tracks",
        "albums",
        "link",
        "description",
        "cover"
})
public class Artist {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("genres")
    private List<String> genres = new ArrayList<>();
    @JsonProperty("tracks")
    private Integer tracks;
    @JsonProperty("albums")
    private Integer albums;
    @JsonProperty("link")
    private String link;
    @JsonProperty("description")
    private String description;
    @JsonProperty("cover")
    private Cover cover;

    /**
     * @return The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The genres
     */
    @JsonProperty("genres")
    public List<String> getGenres() {
        return genres;
    }

    /**
     * @param genres The genres
     */
    @JsonProperty("genres")
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    /*
    generate a semicolon separated string
    * */
    @JsonIgnore
    public String getGenresString() {
        StringBuilder sb = new StringBuilder();
        if (genres.size() == 0)
            return "";
        sb.append(genres.get(0));
        for (int i = 1; i < genres.size(); ++i) {
            sb.append(", ");
            sb.append(genres.get(i));
        }
        return sb.toString();
    }

    /**
     * @return The tracks
     */
    @JsonProperty("tracks")
    public Integer getTracks() {
        return tracks;
    }

    /**
     * @param tracks The tracks
     */
    @JsonProperty("tracks")
    public void setTracks(Integer tracks) {
        this.tracks = tracks;
    }

    /**
     * @return The albums
     */
    @JsonProperty("albums")
    public Integer getAlbums() {
        return albums;
    }

    /**
     * @param albums The albums
     */
    @JsonProperty("albums")
    public void setAlbums(Integer albums) {
        this.albums = albums;
    }

    /**
     * @return The link
     */
    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The cover
     */
    @JsonProperty("cover")
    public Cover getCover() {
        return cover;
    }

    /**
     * @param cover The cover
     */
    @JsonProperty("cover")
    public void setCover(Cover cover) {
        this.cover = cover;
    }
}

