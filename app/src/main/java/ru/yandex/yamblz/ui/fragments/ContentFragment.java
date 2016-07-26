package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import ru.yandex.yamblz.App;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.ui.adapters.CollageAdapter;

public class ContentFragment extends BaseFragment {
    @BindView(R.id.rvCollage)
    protected RecyclerView rvData;

    @Inject
    CollageLoader collageLoader;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.get(this.getContext()).applicationComponent().inject(this);
        rvData.setLayoutManager(new LinearLayoutManager(getContext()));
        rvData.setHasFixedSize(true);
        rvData.setAdapter(new CollageAdapter(collageLoader));
    }
}
