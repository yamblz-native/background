package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import ru.yandex.yamblz.loader.interfaces.ImageCache;

/**
 * Created by platon on 28.07.2016.
 */
public class BitmapCache implements ImageCache<Bitmap>
{
    private static BitmapCache sInstance;
    private LruCache<String, Bitmap> mMemoryCache;

    public static BitmapCache getCache()
    {
        if (sInstance == null)
        {
            sInstance = new BitmapCache();
        }

        return sInstance;
    }

    private BitmapCache()
    {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public void put(String key, Bitmap bitmap)
    {
        if (get(key) == null)
        {
            mMemoryCache.put(key, bitmap);
        }
    }

    @Override
    public Bitmap get(String key)
    {
        return mMemoryCache.get(key);
    }
}
