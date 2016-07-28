package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.GenreLoader;
import ru.yandex.yamblz.model.Genre;
import ru.yandex.yamblz.ui.MyScrollListener;
import ru.yandex.yamblz.ui.adapters.GenreListAdapter;

public class ContentFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Genre>> {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private GenreListAdapter adapter = new GenreListAdapter();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, v);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnScrollListener(new MyScrollListener());

        getLoaderManager().initLoader(1, null, this);
        return v;
    }

    @Override
    public Loader<List<Genre>> onCreateLoader(int id, Bundle args) {
        return new GenreLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Genre>> loader, List<Genre> data) {
        adapter.setDataset(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Genre>> loader) {

    }
}
