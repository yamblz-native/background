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
import ru.yandex.yamblz.loader.square.CollageLoaderSquare;
import ru.yandex.yamblz.model.ArtistFetcher;
import ru.yandex.yamblz.model.ArtistLab;
import ru.yandex.yamblz.model.Genre;
import ru.yandex.yamblz.ui.adapters.ContentGenresRecyclerAdapter;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.fragment_content_recycler_view)
    RecyclerView mRecyclerView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);


        new AsyncTask<Void, Void, Void>() {
            List<Genre> genreList;

            @Override
            protected Void doInBackground(Void... params) {

                genreList = ArtistLab.get(getContext()).getGenresList();
                if (genreList == null || genreList.size() == 0) {
                    try {
                        ArtistLab.get(getContext()).setArtists(new ArtistFetcher().getArtistsFromJson());
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
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                mRecyclerView.setAdapter(new ContentGenresRecyclerAdapter(genreList, inflater, drawable, new CollageLoaderSquare(Picasso.with(context.getApplicationContext()))));
            }
        }.execute();

        return view;
    }
}
