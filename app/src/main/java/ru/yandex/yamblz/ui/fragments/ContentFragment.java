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
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.Genre;
import ru.yandex.yamblz.loader.GenresLoader;
import ru.yandex.yamblz.ui.adapters.GenresAdapter;

public class ContentFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Genre>>{
    @BindView(R.id.rv)
    RecyclerView rv;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new GenresAdapter(getContext()));
    }

    @Override
    public Loader<List<Genre>> onCreateLoader(int id, Bundle args) {
        return new GenresLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Genre>> loader, List<Genre> data) {
        ((GenresAdapter) rv.getAdapter()).changeData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Genre>> loader) {
        ((GenresAdapter) rv.getAdapter()).resetData();
    }
}
