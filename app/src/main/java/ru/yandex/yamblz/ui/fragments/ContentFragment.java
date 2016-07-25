package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.artists.utils.DataSingleton;
import ru.yandex.yamblz.loader.CollageLoaderManager;

public class ContentFragment extends BaseFragment {
    @NonNull
    @BindView(R.id.image_collage) ImageView collage;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> images= DataSingleton.get().getImagesForGenre("rock");
        CollageLoaderManager.getLoader().loadCollage(images,collage,"rock");
        CollageLoaderManager.getLoader().loadCollage(images,collage,"rock");
    }
}
