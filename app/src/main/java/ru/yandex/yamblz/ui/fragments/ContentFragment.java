package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import ru.yandex.yamblz.App;
import ru.yandex.yamblz.ApplicationModule;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.handler.CriticalSectionsHandler;
import ru.yandex.yamblz.models.Genre;
import ru.yandex.yamblz.loader.GenresLoader;
import ru.yandex.yamblz.ui.adapters.GenresAdapter;

public class ContentFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<Genre>>{
    @BindView(R.id.rv)
    RecyclerView rv;

    @Inject @Named(ApplicationModule.MAIN_THREAD_CRITICAL_SECTIONS_HANDLER)
    CriticalSectionsHandler criticalSectionsHandler;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        App.get(getContext()).applicationComponent().inject(this);
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new GenresAdapter(getContext()));
        rv.setOnScrollListener(new RecyclerScrollListener(criticalSectionsHandler));
    }

    @Override
    public Loader<List<Genre>> onCreateLoader(int id, Bundle args) {
        return new GenresLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Genre>> loader, List<Genre> data) {
        ((GenresAdapter) rv.getAdapter()).changeData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Genre>> loader) {
        
    }

    private static class RecyclerScrollListener extends RecyclerView.OnScrollListener {
        CriticalSectionsHandler criticalSectionsHandler;

        public RecyclerScrollListener(CriticalSectionsHandler criticalSectionsHandler) {
            this.criticalSectionsHandler = criticalSectionsHandler;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    criticalSectionsHandler.startSection(0);
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    criticalSectionsHandler.stopSection(0);
                    break;
                default:
                    break;
            }
        }
    }
}
