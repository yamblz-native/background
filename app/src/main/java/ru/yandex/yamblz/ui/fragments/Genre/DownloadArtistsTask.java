package ru.yandex.yamblz.ui.fragments.Genre;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kostya on 26.07.16.
 */

public class DownloadArtistsTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection urlConnection;

        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine())!=null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
