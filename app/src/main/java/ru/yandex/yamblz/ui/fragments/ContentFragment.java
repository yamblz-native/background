package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import retrofit2.Response;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.managers.DataManager;
import ru.yandex.yamblz.models.Genre;
import ru.yandex.yamblz.network.ArtistResponse;
import ru.yandex.yamblz.ui.adapters.GenresAdapter;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContentFragment extends BaseFragment {
    @BindView(R.id.genres_list)
    RecyclerView recyclerView;

    private List<Genre> genres;
    private DataManager dataManager;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                CriticalSectionsHandler criticalSectionsHandler = CriticalSectionsManager.getHandler();

                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    criticalSectionsHandler.startSection(new Random().nextInt());
                } else {
                    criticalSectionsHandler.stopSections();
                }
            }
        });

        dataManager = DataManager.getInstance();
        genres = new ArrayList<>();

        Single.fromCallable(() -> getResult())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(artistsList -> {
                    Map<String, List<String>> map = new HashMap<>();

                    for (ArtistResponse artistResponse : artistsList) {
                        for (String genre : artistResponse.getGenres()) {
                            if (map.get(genre) == null) {
                                List<String> covers = new ArrayList<>();
                                covers.add(artistResponse.getCover().getSmall());
                                map.put(genre, covers);
                            } else {
                                map.get(genre).add(artistResponse.getCover().getSmall());
                            }
                        }
                    }

                    for (String genre : map.keySet()) {
                        Genre genreObject = new Genre(genre, map.get(genre));
                        genres.add(genreObject);
                    }

                    recyclerView.setAdapter(new GenresAdapter(genres));
                });
    }

    private List<ArtistResponse> getResult() throws IOException {
        Response<List<ArtistResponse>> response;
        response = dataManager.getArtistsList().execute();
        return response.body();
    }
}
