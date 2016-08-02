package ru.yandex.yamblz.artists;

import android.os.Bundle;
import android.support.v4.app.Fragment;


//retain fragment для загрузки данные,не имеет ui,нужен только
//для сохранения и доступа к DataLoadingModel
public class DataLoadingFragment extends Fragment {
    private final DataLoadingModel dataLoadingModel;

    public DataLoadingFragment() {
        dataLoadingModel = new DataLoadingModel();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public DataLoadingModel getDataLoadingModel() {
        return dataLoadingModel;
    }
}
