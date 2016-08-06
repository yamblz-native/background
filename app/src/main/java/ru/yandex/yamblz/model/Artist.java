package ru.yandex.yamblz.model;

public class Artist {

    private long id;
    private String name;
    private String[] genres;
    private int tracks;
    private int albums;
    private String link;
    private String description;
    private String smallCoverUrl;
    private String bigCoverUrl;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public void setAlbum(int albums) {
        this.albums = albums;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
        this.description = Character.toString(description.charAt(0)).toUpperCase() + description.substring(1);
    }

    public void setSmallCoverUrl(String smallCoverUrl) {
        this.smallCoverUrl = smallCoverUrl;
    }

    public void setBigCoverUrl(String bigCoverUrl) {
        this.bigCoverUrl = bigCoverUrl;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getGenres() {
        return genres;
    }

    public int getTracks() {
        return tracks;
    }

    public int getAlbum() {
        return albums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getSmallCoverUrl() {
        return smallCoverUrl;
    }

    public String getBigCoverUrl() {
        return bigCoverUrl;
    }

}
