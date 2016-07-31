package ru.yandex.yamblz.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.parser.MyJsonParser;


public class ArtistListFragment extends BaseFragment {
        private DownloadTask downloadTask;
        private RecyclerView mRecyclerView;
        private ArtistAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        public List<Artist> artists;
        public List<Genre> genres;


        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            artists = new ArrayList<>();
            genres = new ArrayList<>();
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_content, container, false);

            mRecyclerView = (RecyclerView) linearLayout.findViewById(R.id.my_recycler_view);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new ArtistAdapter(getActivity());
            downloadTask = new DownloadTask(this);
            downloadTask.execute();



            mAdapter.setItems(genres);

            /*if (savedInstanceState == null) {
              onRetainCustomNonConfigurationInstance();
            } else {
                artists = (List<Artist>) savedInstanceState.getSerializable("listArtist");
                mAdapter.setItems(genres);
            }*/

            mRecyclerView.setAdapter(mAdapter);
            return linearLayout;
        }




        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putSerializable("listArtist", (Serializable) artists);
        }


        public Object onRetainCustomNonConfigurationInstance() {
            return downloadTask;
        }


        private enum Result {
            INPROGRESS, OK, NOARTIST, ERROR
        }






        private class DownloadTask extends AsyncTask<Void, Void, Result> {

            private ArtistListFragment activity = null;
            private Artist artist = null;
            private Result result = Result.INPROGRESS;

            public DownloadTask(ArtistListFragment activity) {
                this.activity = activity;
            }

            public void attachActivity(ArtistListFragment activity) {
                this.activity = activity;
                publishProgress();
            }

            @Override
            protected Result doInBackground(Void... params) {

                Log.i("fxf", "Task started");
                try {

                    MyJsonParser parser = new MyJsonParser();
                    List<Artist> list = parser.parse();
                    Log.i("zaza", "Artists parsed " + list.size());
                    if (list == null) {
                        result = Result.ERROR;
                        return result;
                    } else if (list.size() == 0) {
                        result = Result.NOARTIST;
                        return result;
                    }
                    artists.addAll(list);
                    initListGenres();


                } catch (Exception e) {
                    return Result.ERROR;
                }
                result = Result.OK;
                return result;
            }

            @Override
            protected void onPostExecute(Result res) {
                result = res;
                updateUI();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                updateUI();
            }

            private void updateUI() {
                if (result == Result.OK) {
                    mAdapter.setItems(genres);
                }
            }

            public void initListGenres() {
                int curGunresInt = 0;
                Map<String, Integer> map = new LinkedHashMap<>();
                for (int i = 0; i < artists.size(); i++) {
                    List<String> list = artists.get(i).getListGenres();
                    for (int j = 0; j < list.size(); j++) {
                        if (!map.containsKey(list.get(j))){
                            map.put(list.get(j), curGunresInt++);
                            Genre genre = new Genre();
                            genre.setGenre(list.get(j));
                            genre.addArtist(artists.get(i));
                            genres.add(genre);
                        } else {
                            genres.get(map.get(list.get(j))).addArtist(artists.get(i));
                        }
                    }
                }
            }

        }


    }


