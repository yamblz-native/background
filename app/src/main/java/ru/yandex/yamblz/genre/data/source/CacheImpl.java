package ru.yandex.yamblz.genre.data.source;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Artist;

public class CacheImpl implements Cache<Artist>
{
    private final File cachedFile;

    public CacheImpl(File cacheDir, String name)
    {
        cachedFile = new File(cacheDir, name);
    }

    public CacheImpl(File cacheDir)
    {
        this(cacheDir, "artists.list");
    }

    @SuppressLint("NewApi")
    @Override
    public List<Artist> get()
    {
        try (FileInputStream is = new FileInputStream(cachedFile); ObjectInputStream in = new ObjectInputStream(is))
        {
            return (List<Artist>) in.readObject();
        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void put(List<Artist> list)
    {
        try (FileOutputStream os = new FileOutputStream(cachedFile); ObjectOutputStream out = new ObjectOutputStream(os))
        {
            out.writeObject(list);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean clear()
    {
        return cachedFile.delete();
    }

    @Override
    public boolean isEmpty()
    {
        return !cachedFile.exists();
    }
}
