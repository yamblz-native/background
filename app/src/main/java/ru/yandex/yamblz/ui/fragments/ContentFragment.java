package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.handler.CriticalSectionsManager;
import ru.yandex.yamblz.model.Artist;
import ru.yandex.yamblz.ui.adapters.GenresAdapter;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.rvGenres)
    RecyclerView rvGenres;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(isOnline()){
            initRecycler();
        }else{
            Toast.makeText(getContext(),"No internet connection!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initRecycler() {
        ObjectMapper mapper = new ObjectMapper();
        CriticalSectionsHandler criticalSectionsHandler = CriticalSectionsManager.getHandler();
        try {
            InputStream jsonInputStream = getResources().openRawResource(R.raw.artists);
            List<Artist> artists = mapper.readValue(jsonInputStream, new TypeReference<List<Artist>>() {});
            rvGenres.setLayoutManager(new LinearLayoutManager(getContext()));
            rvGenres.setAdapter(new GenresAdapter(artists));

            rvGenres.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private int sectionsCounter = 0;
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        criticalSectionsHandler.stopSections();
                    }else{
                        criticalSectionsHandler.startSection(++sectionsCounter);
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
