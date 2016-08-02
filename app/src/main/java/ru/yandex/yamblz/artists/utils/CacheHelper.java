package ru.yandex.yamblz.artists.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

//класс для сохранения,получения и удаления строк в файл(кеширования)
public class CacheHelper {
    private static final String TAG="CacheHelper";
    private static final String directory="/CachedStrings/";

    public static void cacheString(Context context,String key,String mJsonResponse) {
        try {
            File checkFile = new File(context.getApplicationInfo().dataDir + directory);
            if (!checkFile.exists()) {
                checkFile.mkdir();
            }
            FileWriter file = new FileWriter(checkFile.getAbsolutePath()+ key);
            file.write(mJsonResponse);
            file.flush();
            file.close();
            Log.i(TAG,"cache write key:"+key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readCacheString(Context context,String key) {
        try {
            File checkFile = new File(context.getApplicationInfo().dataDir + directory);
            File f = new File(checkFile.getAbsolutePath()+ key);
            if (!f.exists()) {
                return null;
            }
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            Log.i(TAG, "cache read key:" + key);
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearCache(Context context) {
        File checkFile = new File(context.getApplicationInfo().dataDir + directory);
        File f = new File(checkFile.getAbsolutePath());
        File[] files = f.listFiles();
        for (File fInDir : files) {
            fInDir.delete();
        }
        Log.i(TAG,"clear cache");
    }

    public static void deleteFile(Context context,String key) {
        File checkFile = new File(context.getApplicationInfo().dataDir + directory);
        File f = new File(checkFile.getAbsolutePath()+ key);
        if (f.exists()) {
            f.delete();
        }
        Log.i(TAG,"delete cache key:"+key);
    }
}
