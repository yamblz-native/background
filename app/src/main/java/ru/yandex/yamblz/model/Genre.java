package ru.yandex.yamblz.model;

import java.util.ArrayList;

public class Genre {

    private String name;
    private ArrayList<String> urls = new ArrayList<>();

    public void setName(String name) {
        this.name = Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

}
