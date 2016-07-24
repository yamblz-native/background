package ru.yandex.yamblz.artists;

import android.database.Observable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.yandex.yamblz.artists.utils.DataSingleton;

/*модель для асинхронной загрузки данных.
Можно подписатся на события:начала загрузки,ошибки,успеха
//при добавление слушателя,если данные сенйчас грузятся,
у слушателя вызавется onLoadingStart
 */
public class DataLoadingModel {


    private static final String TAG = "DataLoadingModel";

    private final DataLoadingObservable mObservable = new DataLoadingObservable();
    private LoadAsyncTask loadingTask;
    private boolean isWorking;

    public DataLoadingModel() {
    }

    public void loadData() {
        if (isWorking) {
            return;
        }
        mObservable.notifyStarted();
        isWorking = true;
        loadingTask = new LoadAsyncTask();
        loadingTask.execute();
    }

    public void stopLoadData() {
        if (isWorking) {
            loadingTask.cancel(true);
            isWorking = false;
        }
    }

    public void registerObserver(final Observer observer) {
        mObservable.registerObserver(observer);
        if (isWorking) {
            observer.onLoadingStart(this);
        }
    }

    public void unregisterObserver(final Observer observer) {
        mObservable.unregisterObserver(observer);
    }

    public interface Observer {
        void onLoadingStart(DataLoadingModel signInModel);

        void onLoadingSucceeded(DataLoadingModel signInModel);

        void onLoadingFailed(DataLoadingModel signInModel);
    }

    private class DataLoadingObservable extends Observable<Observer> {
        public void notifyStarted() {
            for (final Observer observer : mObservers) {
                observer.onLoadingStart(DataLoadingModel.this);
            }
        }

        public void notifySucceeded() {
            for (final Observer observer : mObservers) {
                observer.onLoadingSucceeded(DataLoadingModel.this);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : mObservers) {
                observer.onLoadingFailed(DataLoadingModel.this);
            }
        }
    }
    private class LoadAsyncTask extends AsyncTask<Void, Void, Boolean> {

        protected String loadArtistsFromWeb() {
            URL url = null;
            BufferedReader reader = null;
            String result=null;
            try {
                url = new URL("http://download.cdn.yandex.net/mobilization-2016/artists.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result=buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(reader!=null) try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;

        }

        protected boolean readArtistJson() {
            if (DataSingleton.get().hasData()) {
                Log.i(TAG, "already loaded");
                return true;
            } else {
                String jsonString = loadArtistsFromWeb();
                if (jsonString == null) {
                    Log.i(TAG, "failed to load data");
                    return false;
                } else {
                    DataSingleton.get().setData(jsonString);
                    return true;
                }
            }

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return readArtistJson();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            isWorking = false;
            if (success) {
                Log.i(TAG,"successfully get data");
                mObservable.notifySucceeded();
            } else {
                Log.i(TAG,"error while get data");
                mObservable.notifyFailed();
            }
        }
    }
}
