package ru.yandex.yamblz.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoaderImpl;

public class ContentFragment extends BaseFragment {
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                new CollageLoaderImpl().doo(null);

                return null;
            }
        }.execute();

        return inflater.inflate(R.layout.fragment_content, container, false);
    }
}
