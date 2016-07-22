package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.ApplicationComponent;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.TableCollageStrategy;

public class ContentFragment extends BaseFragment {

    @Inject
    CollageLoader mCollageLoader;
    @BindView(R.id.image)
    ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCollageLoader = getAppComponent().collageLoader();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        ButterKnife.bind(this, view);
        List<String> urls = new ArrayList<>();
        urls.add("http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/300x300");
        urls.add("http://avatars.yandex.net/get-music-content/15ae00fc.p.2915/300x300");
        urls.add("http://avatars.yandex.net/get-music-content/be7f0f49.p.74614/300x300");
        urls.add("http://avatars.yandex.net/get-music-content/40598113.p.1150/300x300");

        mCollageLoader.loadCollage(urls, imageView, null);

        return view;
    }
}
