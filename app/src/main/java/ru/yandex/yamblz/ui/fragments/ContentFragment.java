package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.adapters.CollageAdapter;
import ru.yandex.yamblz.ui.other.ImageType;

import static ru.yandex.yamblz.ui.other.ImageType.*;

public class ContentFragment extends BaseFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CollageAdapter(getImages(), getResources()));

        return view;
    }


    private Map<ImageType, int[]> getImages() {
        Map<ImageType, int[]> images = new HashMap<>();

        images.put(FOREST, new int[]{
                R.drawable.forest_1,
                R.drawable.forest_2,
                R.drawable.forest_3,
                R.drawable.forest_4,
                R.drawable.forest_5,
                R.drawable.forest_6
        });

        images.put(WATER, new int[]{
                R.drawable.water_1,
                R.drawable.water_2,
                R.drawable.water_3,
                R.drawable.water_4,
                R.drawable.water_5,
                R.drawable.land_5,
                R.drawable.mountain_1
        });

        images.put(ANIMAL, new int[]{
                R.drawable.animal_1,
                R.drawable.animal_2,
                R.drawable.animal_3,
                R.drawable.animal_4,
                R.drawable.forest_2,
                R.drawable.mountain_2
        });


        images.put(SUNSET, new int[]{
                R.drawable.car_4,
                R.drawable.desert_2,
                R.drawable.land_3,
                R.drawable.land_4
        });

        images.put(DESERT, new int[]{
                R.drawable.desert_1,
                R.drawable.desert_2,
                R.drawable.desert_3,
                R.drawable.desert_4,
                R.drawable.desert_5,
                R.drawable.car_2
        });

        images.put(MOUNTAIN, new int[]{
                R.drawable.mountain_1,
                R.drawable.mountain_2,
                R.drawable.mountain_3,
                R.drawable.mountain_4,
                R.drawable.mountain_5,
                R.drawable.mountain_6,
                R.drawable.desert_3,
                R.drawable.desert_4
        });


        images.put(LANDSCAPE, new int[]{
                R.drawable.land_1,
                R.drawable.land_2,
                R.drawable.land_3,
                R.drawable.land_4,
                R.drawable.land_5,
                R.drawable.desert_2,
                R.drawable.mountain_1,
                R.drawable.mountain_4
        });

        images.put(CAR, new int[]{
                R.drawable.car_1,
                R.drawable.car_2,
                R.drawable.car_3,
                R.drawable.car_4,
                R.drawable.car_5,
                R.drawable.car_6
        });

        images.put(ONE, new int[]{
                R.drawable.mountain_5
        });

        images.put(FOUR, new int[]{
                R.drawable.mountain_3,
                R.drawable.desert_4,
                R.drawable.land_3,
                R.drawable.forest_3
        });

        images.put(NINE, new int[]{
                R.drawable.mountain_1,
                R.drawable.forest_1,
                R.drawable.forest_2,
                R.drawable.forest_3,
                R.drawable.desert_2,
                R.drawable.desert_3,
                R.drawable.desert_5,
                R.drawable.land_5,
                R.drawable.car_2
        });
        images.put(SIXTEEN, new int[]{
                R.drawable.forest_3,
                R.drawable.forest_6,
                R.drawable.water_1,
                R.drawable.water_4,
                R.drawable.animal_2,
                R.drawable.animal_3,
                R.drawable.desert_3,
                R.drawable.desert_4,
                R.drawable.mountain_2,
                R.drawable.mountain_3,
                R.drawable.land_1,
                R.drawable.land_3,
                R.drawable.land_4,
                R.drawable.car_1,
                R.drawable.car_3,
                R.drawable.car_4
        });

        images.put(TWENTY_FIVE, new int[]{
                R.drawable.forest_3,
                R.drawable.forest_4,
                R.drawable.forest_6,
                R.drawable.water_2,
                R.drawable.water_3,
                R.drawable.water_4,
                R.drawable.animal_1,
                R.drawable.animal_2,
                R.drawable.animal_3,
                R.drawable.desert_1,
                R.drawable.desert_2,
                R.drawable.desert_4,
                R.drawable.desert_5,
                R.drawable.mountain_1,
                R.drawable.mountain_2,
                R.drawable.mountain_4,
                R.drawable.mountain_6,
                R.drawable.land_1,
                R.drawable.land_2,
                R.drawable.land_3,
                R.drawable.land_5,
                R.drawable.car_1,
                R.drawable.car_3,
                R.drawable.car_5,
                R.drawable.car_6
        });

        images.put(THIRTY_SIX, new int[]{
                R.drawable.forest_1,
                R.drawable.forest_2,
                R.drawable.forest_3,
                R.drawable.forest_4,
                R.drawable.forest_5,
                R.drawable.forest_6,
                R.drawable.water_1,
                R.drawable.water_2,
                R.drawable.water_3,
                R.drawable.water_4,
                R.drawable.water_5,
                R.drawable.animal_1,
                R.drawable.animal_2,
                R.drawable.animal_3,
                R.drawable.animal_4,
                R.drawable.desert_1,
                R.drawable.desert_2,
                R.drawable.desert_3,
                R.drawable.desert_4,
                R.drawable.mountain_1,
                R.drawable.mountain_2,
                R.drawable.mountain_3,
                R.drawable.mountain_4,
                R.drawable.mountain_5,
                R.drawable.mountain_6,
                R.drawable.land_1,
                R.drawable.land_2,
                R.drawable.land_3,
                R.drawable.land_4,
                R.drawable.land_5,
                R.drawable.car_1,
                R.drawable.car_2,
                R.drawable.car_3,
                R.drawable.car_4,
                R.drawable.car_5,
                R.drawable.car_6
        });

        return images;
    }
}
