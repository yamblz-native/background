package ru.yandex.yamblz.genre;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.genre.data.entity.Artist;
import ru.yandex.yamblz.genre.data.source.Cache;
import ru.yandex.yamblz.genre.data.source.CacheImpl;
import ru.yandex.yamblz.genre.data.source.DataSource;
import ru.yandex.yamblz.genre.data.source.RemoteDataSource;
import ru.yandex.yamblz.genre.data.source.Repository;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.SimpleCollageLoader;
import ru.yandex.yamblz.genre.adapter.CollageAdapter;
import ru.yandex.yamblz.genre.data.entity.Genre;
import ru.yandex.yamblz.genre.interfaces.GenresPresenter;
import ru.yandex.yamblz.genre.interfaces.GenresView;
import ru.yandex.yamblz.ui.fragments.BaseFragment;

public class GenresFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, GenresView {

    private static final String TAG = "GenresFragment";

    private CollageLoader collageLoader;
    private GenresPresenter<GenresView> presenter;
    private CollageAdapter collageAdapter;
    private Unbinder unbinder;

    @BindView(R.id.rv_collages)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_view)
    SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Handler responseHandler = new Handler();
        CollageLoaderManager.init(new SimpleCollageLoader(responseHandler));
        collageLoader = CollageLoaderManager.getLoader();

        File cacheDir = getActivity().getCacheDir();
        Cache<Artist> cache = new CacheImpl(cacheDir);
        DataSource remoteDataSource = new RemoteDataSource();

        presenter = new GenresPresenterImpl(Repository.getInstance(cache, remoteDataSource));
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle)
    {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        collageAdapter = new CollageAdapter(collageLoader);
        recyclerView.setAdapter(collageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        presenter.bind(this);
        presenter.getGenres(false);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        presenter.unsubscribe();
        presenter.unbind();
    }

    @Override
    public void onDestroyView()
    {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void showProgress(boolean show)
    {
        swipeLayout.setRefreshing(show);
    }

    @Override
    public void showGenres(List<Genre> genresList) {
        collageAdapter.swap(genresList);
    }

    @Override
    public void showError(String error)
    {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh()
    {
        collageAdapter.clear();
        presenter.getGenres(true);
    }
}
