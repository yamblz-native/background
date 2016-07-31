package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ContentFragment extends BaseFragment {
    @BindView(R.id.main_recycler)
    RecyclerView recycler;

    ArtistsLoadingPresenter presenter;
    String baseUrl = "http://cache-default03g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/";
    private Retrofit retrofit;
    private ArtistsApi artistsApi;
    private GenreAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lazy libraries initialization
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
                .build();

         retrofit = new Retrofit.Builder()
                 .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        artistsApi = retrofit.create(ArtistsApi.class);

        presenter = new ArtistsLoadingPresenter(artistsApi);
        presenter.bindView(this);
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
        adapter = new GenreAdapter(getContext());
        recycler.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unbindView(this);
    }

    public void showContent(List<Genre> genreList) {
        adapter.setContent(genreList);
    }
}
