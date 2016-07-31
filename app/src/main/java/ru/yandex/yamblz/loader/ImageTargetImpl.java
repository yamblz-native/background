package ru.yandex.yamblz.loader;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import ru.yandex.yamblz.loader.interfaces.ImageTarget;

/**
 * Created by platon on 26.07.2016.
 */
public class ImageTargetImpl implements ImageTarget
{
    private static final long DURATION = 300;
    private static final int EMPTY = 0;
    private static final String TAG = "ImageTargetImpl";

    private final WeakReference<ImageView> weakReference;

    public ImageTargetImpl(ImageView imageView)
    {
        imageView.setImageResource(EMPTY);
        weakReference = new WeakReference<>(imageView);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap)
    {
        ImageView imageView = weakReference.get();

        if (imageView != null && bitmap != null)
        {
            Log.d(TAG, "onLoadBitmap");
            animateChange(imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public ImageView getImageView()
    {
        return weakReference.get();
    }

    @Override
    public void clear()
    {
        weakReference.clear();
    }

    private void animateChange(final ImageView imageView)
    {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0.0f, 1.0f);
        fadeIn.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(DURATION).play(fadeIn);
        animatorSet.start();
    }
}
