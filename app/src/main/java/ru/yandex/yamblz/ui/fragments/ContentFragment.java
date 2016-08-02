package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.loader.CollageLoaderManager;
import ru.yandex.yamblz.loader.square.CollageLoaderSquare;
import ru.yandex.yamblz.model.ArtistFetcher;
import ru.yandex.yamblz.model.ArtistLab;
import ru.yandex.yamblz.model.Genre;
import ru.yandex.yamblz.ui.adapters.ContentGenresRecyclerAdapter;

public class ContentFragment extends BaseFragment {
    private CriticalSectionsHandler mHandler;

    @BindView(R.id.fragment_content_recycler_view)
    RecyclerView mRecyclerView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mHandler = CriticalSectionsManager.getHandler();
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);

        // Вот сделаю ДЗ с БД и наступит мне счастье. А пока только так
        new AsyncTask<Void, Void, Void>() {
            List<Genre> genreList;

            @Override
            protected Void doInBackground(Void... params) {

                ArtistLab artistLab = ArtistLab.get(getContext());
                genreList = artistLab.getGenresList();
                if (genreList == null || genreList.size() == 0) {
                    try {
                        artistLab.setArtists(new ArtistFetcher().getArtistsFromJson());
                        genreList = artistLab.getGenresList();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Context context = getContext();
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_album_black_240dp);

                CollageLoaderManager.init(new CollageLoaderSquare(Picasso.with(context.getApplicationContext())));

                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                mRecyclerView.setAdapter(new ContentGenresRecyclerAdapter(genreList, inflater, drawable, CollageLoaderManager.getLoader()));
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            mHandler.stopSection(1);
                        }
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            mHandler.startSection(1);
                        }
                    }
                });
            }
        }.execute();

        return view;
    }
}
