package ru.yandex.yamblz.utils;


import java.util.List;

public class StringUtils {
    public static String getGenres(List<String> genres){
        if (genres == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String genre : genres) {
            sb.append(genre);
            sb.append(", ");
        }
        if (sb.length() >= 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }
}
