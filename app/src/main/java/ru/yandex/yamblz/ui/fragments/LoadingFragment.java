package ru.yandex.yamblz.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.artists.DataLoadingFragment;
import ru.yandex.yamblz.artists.DataLoadingModel;
import ru.yandex.yamblz.artists.DefaultLoadingObserver;

import static android.content.ContentValues.TAG;

public class LoadingFragment extends Fragment implements DataLoadingModel.Observer {

    private DataLoadingModel dataLoadingModel;
    private DefaultLoadingObserver defaultLoadingObserver;

    public void load() {
        Log.d(TAG, "loading");
        String TAG_DATA_LOADING = "TAG_DATA_LOADING";
        //получаем фрагмент для загрузки данных(если он уже был создан)
        final DataLoadingFragment retainDataLoadingFragment =
                (DataLoadingFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TAG_DATA_LOADING);
        if (retainDataLoadingFragment != null) {
            Log.d(TAG, "has data model");
            dataLoadingModel = retainDataLoadingFragment.getDataLoadingModel();
        } else {
            Log.d(TAG, "no data model");
            final DataLoadingFragment dataLoadingFragment = new DataLoadingFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(dataLoadingFragment, TAG_DATA_LOADING)
                    .commit();
            dataLoadingModel = dataLoadingFragment.getDataLoadingModel();
        }
        defaultLoadingObserver = new DefaultLoadingObserver(getContext());
        dataLoadingModel.registerObserver(defaultLoadingObserver);
        dataLoadingModel.registerObserver(this);
        dataLoadingModel.loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataLoadingModel.unregisterObserver(defaultLoadingObserver);
        dataLoadingModel.unregisterObserver(this);
        defaultLoadingObserver.dispose();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load();
    }

    @Override
    public void onLoadingStart(DataLoadingModel signInModel) {

    }

    @Override
    public void onLoadingSucceeded(DataLoadingModel signInModel) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout,new ContentFragment()).commit();
    }

    @Override
    public void onLoadingFailed(DataLoadingModel signInModel) {
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.main_frame_layout,new InternetErrorFragment()).commit();
    }
}
