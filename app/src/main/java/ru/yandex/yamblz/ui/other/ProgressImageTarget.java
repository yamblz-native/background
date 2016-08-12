package ru.yandex.yamblz.ui.other;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ru.yandex.yamblz.loader.ImageTarget;


public class ProgressImageTarget implements ImageTarget {
    private ImageView imageView;
    private ProgressBar progressBar;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public ProgressImageTarget(ImageView imageView, ProgressBar progressBar) {
        this.imageView = imageView;
        this.progressBar = progressBar;
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadBitmap(Bitmap bitmap) {
        mainHandler.post(() -> {
            progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
        });
    }
}
