package ru.yandex.yamblz.managers;

import java.util.List;

import retrofit2.Call;
import ru.yandex.yamblz.network.ArtistResponse;
import ru.yandex.yamblz.network.ServiceGenerator;
import ru.yandex.yamblz.network.YandexService;

/**
 * Created by shmakova on 29.07.16.
 */

public class DataManager {
    private static DataManager INSTANCE = null;
    private YandexService yandexService;

    public DataManager() {
        this.yandexService = ServiceGenerator.createService(YandexService.class);
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }

        return INSTANCE;
    }


    public Call<List<ArtistResponse>> getArtistsList() {
        return yandexService.getArtistsList();
    }

}