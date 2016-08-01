package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.util.Pair;

import java.util.List;
import java.util.Random;

/**
 * Created by Александр on 25.07.2016.
 */

public class RandomCollageStrategy implements CollageStrategy {
    private final int height;
    private final int wight;
    private final Random random;
    private final float randomCoff = 0.6f;

    public RandomCollageStrategy(int height, int wight) {
        this.height = height;
        this.wight = wight;
        this.random = new Random(System.currentTimeMillis() + height + wight);
    }

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        Bitmap result = Bitmap.createBitmap(wight, height, Bitmap.Config.RGB_565);
        Canvas convas = new Canvas(result);
        for (Bitmap b : bitmaps){
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, height / 2, wight / 2, false);
            int i = random.nextInt(90) - 45;
            convas.rotate(i);
            convas.drawBitmap(scaledBitmap, nextLeft(), nextTop(), null);
            convas.rotate(-i);
        }
        return result;
    }

    private float nextLeft() {
        return random.nextInt((int) (wight * randomCoff));
    }

    private float nextTop() {
        return random.nextInt((int) (height * randomCoff));
    }
}
