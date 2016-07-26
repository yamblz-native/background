package ru.yandex.yamblz.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 * Stores data about one artist.
 */
@SuppressWarnings("WeakerAccess")
public class Artist implements Parcelable {
    public int id;
    public String name;
    public List<String> genres;
    public int tracks, albums;
    public String link;
    public String description;
    public String smallCover, bigCover;

    public Artist() {
    }

    /**
     * Returns genres as a comma-separated string.
     */
    public String getGenres() {
        return TextUtils.join(", ", genres);
    }

    /**
     * Returns link to small cover at current row of cursor,
     */
    public static String getSmallCover(Cursor cursor) {
        return cursor.getString(7);
    }

    /**
     * Returns link to big cover at current row of cursor,
     */
    public static String getBigCover(Cursor cursor) {
        return cursor.getString(8);
    }

    /**
     * Creates an instance from parcel.
     * @param in parcel.
     */
    protected Artist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        genres = in.createStringArrayList();
        tracks = in.readInt();
        albums = in.readInt();
        link = in.readString();
        description = in.readString();
        smallCover = in.readString();
        bigCover = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeStringList(genres);
        dest.writeInt(tracks);
        dest.writeInt(albums);
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(smallCover);
        dest.writeString(bigCover);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
