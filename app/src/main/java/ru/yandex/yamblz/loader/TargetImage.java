package ru.yandex.yamblz.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TargetImage extends ImageView implements ImageTarget {
    public TargetImage(Context context) {
        super(context);
    }

    public TargetImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        setAlpha(0f);
        setImageBitmap(bitmap);
        animate().alpha(1);
    }
}
