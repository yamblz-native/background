package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

/**
 * Created by SerG3z on 02.08.16.
 */

public class MyCollageStrategy implements CollageStrategy {

    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        int size = bitmaps.size();
        int sqr = (int) Math.sqrt(size);

        Bitmap bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (size >= 4) {
            if (sqr > 10) {
                sqr = 10;
            }
            int width = bitmap.getWidth() / sqr;
            int index = 0;
            Rect rect;
            for (int i = 0; i < sqr; i++) {
                for (int j = 0; j < sqr; j++) {
//                    first version
//                    canvas.drawBitmap(getResizedBitmap(bitmaps.get(index++), width, width), j * width, i * width, null);
                    rect = new Rect(j * width, i * width, j * width + width, i * width + width);
                    canvas.drawBitmap(bitmaps.get(index++), null, rect, null);
                }
            }
            canvas.save();
            return bitmap;
        } else if (size == 3) {
            canvas.drawBitmap(bitmaps.get(0), null, new Rect(0, 0, 298, 298), null);
            canvas.drawBitmap(bitmaps.get(1), null, new Rect(302, 0, 600, 298), null);
            canvas.drawBitmap(bitmaps.get(2), null, new Rect(150, 302, 450, 600), null);
            canvas.save();
            return bitmap;
        } else if (size == 2) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            canvas.drawBitmap(bitmaps.get(0), null, new Rect(0, 0, 600, 600), null);
            canvas.drawRect(new Rect(300, 300, 600, 600), paint);  //for beauty
            canvas.drawBitmap(bitmaps.get(1), null, new Rect(305, 305, 595, 595), null);
            canvas.save();
            return bitmap;
        } else {
            canvas.drawBitmap(bitmaps.get(0), 0, 0, null);
            canvas.save();
            return bitmaps.get(0);
        }
    }
//
//      first version
//    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
//        int width = bm.getWidth();
//        int height = bm.getHeight();
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        Timber.d("scaleW = " + scaleWidth);
//        Timber.d("scaleH = " + scaleHeight);
//        if (scaleHeight >= 1.0 && scaleWidth >= 1.0) {
//            return bm;
//        }
//        Matrix matrix = new Matrix();
//        matrix.postScale(scaleWidth, scaleHeight);
//        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
//        if (bm != null && !bm.isRecycled()) {
//            bm.recycle();
//            bm = null;
//        }
//        return resizedBitmap;
//    }
}
