package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.data.ArtistsApi;
import ru.yandex.yamblz.data.Genre;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import timber.log.Timber;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class ContentFragment extends BaseFragment {
    @BindView(R.id.main_recycler)
    RecyclerView recycler;

    private ArtistsLoadingPresenter presenter;
    private GenreAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Use dagger
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        String baseUrl = getString(R.string.base_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ArtistsApi artistsApi = retrofit.create(ArtistsApi.class);

        presenter = new ArtistsLoadingPresenter(artistsApi);
        presenter.bindView(this);
        Timber.d("Loading artists");
        presenter.loadArtists();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GenreAdapter();
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(scrollingListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CollageLoaderManager.getLoader().destroyAll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unbindView(this);
    }

    void showContent(List<Genre> genreList) {
        adapter.setContent(genreList);
    }

    private final OnScrollListener scrollingListener = new OnScrollListener() {
        boolean scrollingStarted;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    Timber.d("Stopped scrolling");
                    CriticalSectionsManager.getHandler().stopSection(0);
                    scrollingStarted = false;
                    break;
                case SCROLL_STATE_DRAGGING:
                    if (!scrollingStarted) {
                        Timber.d("Started scrolling");
                        CriticalSectionsManager.getHandler().startSection(0);
                        scrollingStarted = true;
                    }
                    break;
            }
        }
    };
}
