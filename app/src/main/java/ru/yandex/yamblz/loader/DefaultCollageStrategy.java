package ru.yandex.yamblz.loader;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class DefaultCollageStrategy implements CollageStrategy {
    @Override
    public Bitmap create(List<Bitmap> bitmaps) {
        //lenght in small bitmaps
        int number=(int)Math.sqrt(bitmaps.size());
        //подразумевается что все битмапы имеют одинаковый размер
        int width=bitmaps.get(0).getWidth()/number;
        int height=bitmaps.get(0).getHeight()/number;
        int bitmapWidth=width*number;
        int bitmapHeight=height*number;
        float widthScale=(float)width/bitmapWidth;
        float heightScale=(float)height/bitmapHeight;
        Bitmap.Config conf = Bitmap.Config.RGB_565; // see other conf types
        //mutable bitmap
        //новый битмап может быть чуть меньше одной обложки,тк размер не всегда делится нацело
        Bitmap bmp = Bitmap.createBitmap(width*number, height*number, conf);
        Canvas canvas = new Canvas(bmp);
        canvas.scale(widthScale,heightScale);
        for(int y=0;y<number;y++){
            for(int x=0;x<number;x++){
                Bitmap bitmap=bitmaps.get(y*number+x);
                canvas.drawBitmap(bitmap,x*bitmapWidth,y*bitmapHeight,null);
                bitmap.recycle();
            }
        }
        return bmp;
    }
}
