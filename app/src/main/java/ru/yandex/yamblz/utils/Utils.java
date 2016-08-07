package ru.yandex.yamblz.utils;

import java.util.Random;

/**
 * Created by grin3s on 07.08.16.
 */

public class Utils {
    private static Random rnd = new Random();

    public static int getRandomCoordinate(int start, int end) {
        return (int) Math.floor(rnd.nextFloat() * (end - start) + start);
    }
}
