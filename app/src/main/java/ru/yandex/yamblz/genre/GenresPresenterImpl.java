package ru.yandex.yamblz.genre;

import java.util.List;

import ru.yandex.yamblz.genre.data.entity.Genre;
import ru.yandex.yamblz.genre.data.source.DataSource;
import ru.yandex.yamblz.genre.interfaces.GenresPresenter;
import ru.yandex.yamblz.genre.interfaces.GenresView;
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

    public GenresPresenterImpl(DataSource dataSource)
    {
        this.dataSource = dataSource;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void getGenres(boolean forceLoad)
    {
        if (NetworkManager.getManager().networkIsAvailable())
        {
            genresView.showProgress(true);
            if (forceLoad) dataSource.delete();

            subscriptions.add(dataSource.getList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(Utils::transformArtistToGenres)
                    .subscribe(onNext, onError));
        }
        else
        {
            genresView.showProgress(false);
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

    private Action1<List<Genre>> onNext = genres -> {
        genresView.showProgress(false);
        genresView.showGenres(genres);
    };

    private Action1<Throwable> onError = e -> {
        genresView.showProgress(false);
        genresView.showError(e.getMessage());
    };
}
