package ru.yandex.yamblz.loader;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ru.yandex.yamblz.genre.util.BitmapUtils;
import ru.yandex.yamblz.loader.interfaces.ImageCache;

/**
 * Created by platon on 27.07.2016.
 */
public class ImageDownloader implements Runnable
{
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final String REQUEST_METHOD = "GET";
    private static final int REQ_WIDTH = 100;
    private static final int REQ_HEIGHT = 100;

    private final List<Bitmap> bitmaps;
    private final String url;
    private final CountDownLatch countDownLatch;
    private ImageCache<Bitmap> bitmapCache;

    public ImageDownloader(String url, List<Bitmap> bitmaps, CountDownLatch countDownLatch)
    {
        this.countDownLatch = countDownLatch;
        bitmapCache = BitmapCache.getCache();
        this.bitmaps = bitmaps;
        this.url = url;
    }

    @Override
    public void run()
    {
        if (bitmapCache.get(url) != null)
        {
            bitmaps.add(cachedBitmap());
        }
        else
        {
            bitmaps.add(remoteBitmap());
        }

        countDownLatch.countDown();
    }

    private Bitmap remoteBitmap()
    {
        Log.d("Downloader", "fromRemote");
        HttpURLConnection conn = null;
        Bitmap bitmap = null;

        try
        {
            conn = getConnection(url);
            byte[] bitmapBytes = getBytes(conn.getInputStream());
            bitmap = BitmapUtils.decodeBitmapFromByte(bitmapBytes, 0, bitmapBytes.length, REQ_WIDTH, REQ_HEIGHT);
            bitmapCache.put(url, bitmap);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (conn != null) { conn.disconnect(); }
        }

        return bitmap;
    }

    private Bitmap cachedBitmap()
    {
        Log.d("Downloader", "fromCache");
        return bitmapCache.get(url);
    }

    private byte[] getBytes(InputStream stream) throws IOException
    {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = stream.read(buffer)) != -1)
        {
            result.write(buffer, 0, length);
        }

        return result.toByteArray();
    }

    private HttpURLConnection getConnection(String urlString) throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setRequestMethod(REQUEST_METHOD);
        connection.setDoInput(true);

        connection.connect();
        return connection;
    }
}
