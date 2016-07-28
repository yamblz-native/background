package ru.yandex.yamblz.loader;

import android.widget.ImageView;

import java.util.List;

import ru.yandex.yamblz.loader.interfaces.CollageLoader;
import ru.yandex.yamblz.loader.interfaces.CollageStrategy;
import ru.yandex.yamblz.loader.interfaces.ImageTarget;

public class StubCollageLoader implements CollageLoader
{
    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {}

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {}

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy strategy) {}

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy strategy) {}
}
