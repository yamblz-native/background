package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.tasks.LoadSingersTask;
import ru.yandex.yamblz.models.Singer;
import ru.yandex.yamblz.api.SingersApi;
import ru.yandex.yamblz.loader.TableCollageStrategy;
import ru.yandex.yamblz.ui.adapters.GenresAdapter;

public class ContentFragment extends BaseFragment implements LoadSingersTask.Callbacks {

    @Inject
    CollageLoader mCollageLoader;

    @Inject
    OkHttpClient mOkHttpClient;

    @Inject
    SingersApi mSingersApi;

    @Inject
    CriticalSectionsHandler mCriticalSectionsHandler;

    @BindView(R.id.genres)
    RecyclerView genres;

    private LoadSingersTask mSingersTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mSingersTask = new LoadSingersTask(this, mSingersApi);
        mSingersTask.execute();

    }

    @Override
    public void onStop() {
        super.onStop();
        mSingersTask.cancel(false);
    }

    @Override
    public void onSingers(@Nullable List<Singer> singers) {
        if(singers == null) {
            Snackbar.make(genres, getString(R.string.error), Snackbar.LENGTH_LONG).show();
        } else {
            genres.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        mCriticalSectionsHandler.startSection(0);
                    } else if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mCriticalSectionsHandler.stopSection(0);
                    }
                }
            });
            genres.setLayoutManager(new LinearLayoutManager(getContext()));
            genres.setAdapter(new GenresAdapter(Singer.collectGenres(singers),
                    mCollageLoader, new TableCollageStrategy(), mCriticalSectionsHandler));
        }
    }


}
