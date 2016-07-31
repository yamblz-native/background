package ru.yandex.yamblz.tasks;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

import ru.yandex.yamblz.api.SingersApi;
import ru.yandex.yamblz.models.Singer;

/**
 * Async task for loading singers
 */
public final class LoadSingersTask extends AsyncTask<Void, Void, List<Singer>> {

    private WeakReference<Callbacks> mCallbacks;
    private SingersApi mSingersApi;

    public interface Callbacks {
        void onSingers(@Nullable List<Singer> singers);
    }

    public LoadSingersTask(Callbacks callbacks, SingersApi singersApi) {
        this.mCallbacks = new WeakReference<>(callbacks);
        this.mSingersApi = singersApi;
    }

    @Override
    protected List<Singer> doInBackground(Void... params) {
        return mSingersApi.getSingers();
    }

    @Override
    protected void onPostExecute(List<Singer> singers) {
        Callbacks callbacks = mCallbacks.get();
        if(callbacks != null) {
            callbacks.onSingers(singers);
        }
    }
}