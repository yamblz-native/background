package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.yandex.yamblz.R;

//при отсутствие интернета, показываем этот фрагмент
public class InternetErrorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.internet_error_fragment, container, false);
        view.findViewById(R.id.reconnect_button).setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame_layout, new LoadingFragment()).commit());
        return view;
    }
}
