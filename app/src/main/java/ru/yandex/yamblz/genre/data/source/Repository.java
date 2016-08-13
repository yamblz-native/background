package ru.yandex.yamblz.genre.data.source;

import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Artist;
import ru.yandex.yamblz.genre.data.entity.Genre;
import ru.yandex.yamblz.genre.data.source.local.ICache;
import ru.yandex.yamblz.genre.util.Utils;
import rx.Observable;

/**
 * Created by platon on 28.07.2016.
 */
public class Repository implements DataSource
{
    private ICache<Artist> cache;
    private DataSource remote;

    public Repository(ICache<Artist> cache, DataSource remote)
    {
        this.cache = cache;
        this.remote = remote;
    }

    @Override
    public Observable<List<Artist>> getArtists()
    {
        if (cache.isEmpty()) return fromRemote();
        return fromLocal();
    }

    @Override
    public Observable<List<Genre>> getGenres()
    {
        return getArtists().map(Utils::transformArtistToGenres);
    }

    @Override
    public void delete()
    {
        cache.clear();
    }

    private Observable<List<Artist>> fromRemote()
    {
        return remote.getArtists().doOnNext(artists -> cache.put(artists));
    }

    private Observable<List<Artist>> fromLocal()
    {
        return Observable.just(cache.get());
    }
}
