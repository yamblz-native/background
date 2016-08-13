package ru.yandex.yamblz.genre.data.source.local;


import java.io.File;
import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Artist;


/**
 * Created by platon on 19.07.2016.
 */
public class CacheImpl implements ICache<Artist>
{
    private final File cachedFile;
    private final JsonSerializer serializer;
    private final FileManager fileManager;

    public CacheImpl(File cacheDir, String name, JsonSerializer artistCacheSerializer)
    {
        cachedFile = new File(cacheDir, name);
        fileManager = new FileManager();
        serializer = artistCacheSerializer;
    }

    public CacheImpl(File cacheDir, JsonSerializer artistCacheSerializer)
    {
        this(cacheDir, "artists.list", artistCacheSerializer);
    }

    @Override
    public List<Artist> get()
    {
        String json = fileManager.readFileContent(cachedFile);
        return serializer.deserialize(json);
    }

    @Override
    public void put(List<Artist> list)
    {
        fileManager.writeToFile(cachedFile, serializer.serialize(list));
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
