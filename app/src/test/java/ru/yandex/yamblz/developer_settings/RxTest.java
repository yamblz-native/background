package ru.yandex.yamblz.developer_settings;

import android.provider.Settings;
import android.util.Log;

import org.junit.Test;

import rx.Observable;

/**
 * Created by Volha on 05.08.2016.
 */

public class RxTest {

    @Test
    public void rxFilterTest() {

        Observable.range(0, 10)
                .flatMap( i -> {
                    if ( i % 2 == 0)
                        return Observable.just(i);
                    else
                        return Observable.empty();
                })
                .subscribe(it -> System.out.println(it));
    }

}
