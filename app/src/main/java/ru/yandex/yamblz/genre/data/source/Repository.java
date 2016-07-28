package ru.yandex.yamblz.genre.data.source;

import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Artist;
import rx.Observable;

/**
 * Created by platon on 28.07.2016.
 */
public class Repository implements DataSource
{
    private static Repository sRepository;

    private Cache<Artist> cache;
    private DataSource remote;

    public static Repository getInstance(Cache<Artist> cache, DataSource remote)
    {
        if (sRepository == null)
        {
            sRepository = new Repository(cache, remote);
        }
        return sRepository;
    }

    private Repository(Cache<Artist> cache, DataSource remote)
    {
        this.cache = cache;
        this.remote = remote;
    }

    @Override
    public Observable<List<Artist>> getList()
    {
        if (cache.isEmpty()) return fromRemote();
        return fromLocal();
    }

    @Override
    public void delete()
    {
        cache.clear();
    }

    private Observable<List<Artist>> fromRemote()
    {
        return remote.getList().doOnNext(artists -> cache.put(artists));
    }

    private Observable<List<Artist>> fromLocal()
    {
        return Observable.just(cache.get());
    }
}
