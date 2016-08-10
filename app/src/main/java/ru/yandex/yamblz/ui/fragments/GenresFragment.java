package ru.yandex.yamblz.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.yandex.yamblz.App;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Genre;
import ru.yandex.yamblz.handler.Task;
import ru.yandex.yamblz.ui.adapters.IRetrieveGenreImage;
import ru.yandex.yamblz.ui.adapters.PerformersAdapter;
import ru.yandex.yamblz.ui.presenters.GenresPresenter;


/**
 * Created by dalexiv on 7/18/16.
 */

public class GenresFragment extends BaseFragment implements IDisplayPerformers {
    // Layout
    @BindView(R.id.recyclerPerfs)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    GenresPresenter presenter;
    private Unbinder unbinder;
    private PerformersAdapter adapter;

    public static GenresFragment newInstance() {
        return new GenresFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genres, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.get(getActivity()).applicationComponent().inject(this);
        unbinder = ButterKnife.bind(this, view);
        presenter.bindView(this);

        // Refactor into interface
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("Исполнители");
        }

        configureRecyclerViewAndAdapter();
        setupSwipeToRefresh();

    }

    private void setupSwipeToRefresh() {
        // Clearing old data and loading new one from network
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.doSwipeToRefresh());

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                Color.YELLOW, ContextCompat.getColor(getActivity(), R.color.colorAccent));
    }

    private void configureRecyclerViewAndAdapter() {
        // Initializing adapter
        adapter = new PerformersAdapter(this, new IRetrieveGenreImage() {
            @Override
            public void postDownloadingTask(Genre genre, ImageView toPost) {
                presenter.retrieveGenreSmallImage(genre, toPost);
            }

            @Override
            public void removeOldTask(Task task) {

            }
        });

        // Configuring recyclerview
        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    presenter.stopScrolling();
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                    presenter.startScrolling();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unbindView(this);
        unbinder.unbind();
        adapter = null;
    }

    @Override
    public void setRefreshing(boolean isRefreshing) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.post(() -> {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(isRefreshing);
            });
    }

    @Override
    public void notifyUser(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void addGenre(Genre genre) {
        adapter.addGenre(genre);
    }

    @Override
    public void clearPerformers() {
        adapter.clearPerformers();
    }
}
