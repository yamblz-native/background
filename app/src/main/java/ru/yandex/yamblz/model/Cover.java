package ru.yandex.yamblz.model;


import java.io.Serializable;

public class Cover implements Serializable{

    private static final long serialVersionUID = 432342L;

    private String small;
    private String big;

    public Cover() {
    }

    public Cover(String small, String big) {
        this.small = small;
        this.big = big;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }
}
