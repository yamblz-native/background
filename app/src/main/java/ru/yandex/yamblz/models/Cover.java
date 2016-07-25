package ru.yandex.yamblz.models;

public class Cover {
    private String small;

    private String big;

    public Cover(String small, String big) {
        this.small = small;
        this.big = big;
    }

    public String getSmall() {
        return small;
    }

    public String getBig() {
        return big;
    }
}
