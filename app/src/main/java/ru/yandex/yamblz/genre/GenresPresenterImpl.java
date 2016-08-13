package ru.yandex.yamblz.genre;

import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Genre;
import ru.yandex.yamblz.genre.data.source.DataSource;
import ru.yandex.yamblz.genre.interfaces.GenresPresenter;
import ru.yandex.yamblz.genre.interfaces.GenresView;
import ru.yandex.yamblz.genre.util.INetworkManager;
import ru.yandex.yamblz.genre.util.NetworkManager;
import ru.yandex.yamblz.genre.util.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by platon on 27.07.2016.
 */
public class GenresPresenterImpl implements GenresPresenter<GenresView>
{
    private GenresView genresView;
    private CompositeSubscription subscriptions;
    private DataSource dataSource;
    private INetworkManager networkManager;

    public GenresPresenterImpl(DataSource dataSource)
    {
        this.dataSource = dataSource;
        networkManager = NetworkManager.getManager();
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void getGenres(boolean forceLoad)
    {
        if (networkManager.networkIsAvailable())
        {
            genresView.showProgress(true);
            if (forceLoad) dataSource.delete();

            subscriptions.add(dataSource.getGenres()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onNext, onError));
        }
        else
        {
            genresView.showError("no connection");
        }
    }

    @Override
    public void bind(GenresView view)
    {
        genresView = view;
    }

    @Override
    public void unbind()
    {
        genresView = null;
    }

    @Override
    public void unsubscribe()
    {
        subscriptions.clear();
    }

    private Action1<List<Genre>> onNext = genres ->
    {
        genresView.showProgress(false);
        genresView.showGenres(genres);
    };

    private Action1<Throwable> onError = e ->
    {
        genresView.showProgress(false);
        genresView.showError(e.getMessage());
    };
}
