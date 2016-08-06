package ru.yandex.yamblz.loader.square;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.yandex.yamblz.loader.CollageLoader;
import ru.yandex.yamblz.loader.CollageStrategy;
import ru.yandex.yamblz.loader.ImageTarget;
import ru.yandex.yamblz.loader.ImageTargetWithId;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CollageLoaderSquare implements CollageLoader {
    private static final int NUMBER_OF_THREADS = 2;
    private final CollageStrategySquare DEFAULT_COLLAGE_STRATEGY = new CollageStrategySquare();
    private final Picasso mPicasso;
    private final SparseArray<Subscription> mSubscriptionMap = new SparseArray<>(); // Хранит подписки

    public CollageLoaderSquare(Picasso picasso) {
        mPicasso = picasso;
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView) {
        loadCollage(urls, imageView, DEFAULT_COLLAGE_STRATEGY);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget) {
        loadCollage(urls, imageTarget, DEFAULT_COLLAGE_STRATEGY);
    }

    @Override
    public void loadCollage(List<String> urls, ImageView imageView, CollageStrategy collageStrategy) {
        WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(imageView);
        ImageTarget imageTarget = new ImageTargetWithId() {
            @Override
            public void onLoadBitmap(Bitmap bitmap) {
                ImageView targetImageView = imageViewWeakReference.get();
                if (targetImageView != null) {
                    targetImageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public int getId() {
                ImageView targetImageView = imageViewWeakReference.get();
                if (targetImageView != null) {
                    return targetImageView.hashCode();
                } else {
                    return super.getId();
                }
            }
        };

        loadCollage(urls, imageTarget, collageStrategy);
    }

    @Override
    public void loadCollage(List<String> urls, ImageTarget imageTarget, CollageStrategy collageStrategy) {
        int imageTargetId;
        if (imageTarget instanceof ImageTargetWithId) {
            imageTargetId = ((ImageTargetWithId) imageTarget).getId();
        } else {
            imageTargetId = imageTarget.hashCode();
        }

        Subscription subscription = mSubscriptionMap.get(imageTargetId);
        if (subscription != null) {
            subscription.unsubscribe();
        }

        Action1<Bitmap> onCreateCollage = imageTarget::onLoadBitmap;

        Subscription newSubscription = makeCollage(urls, onCreateCollage, collageStrategy);
        mSubscriptionMap.put(imageTargetId, newSubscription);
    }

    private Subscription makeCollage(List<String> urls, Action1<Bitmap> onCreateCollage, CollageStrategy collageStrategy) {
        final AtomicInteger counter = new AtomicInteger();
        final List<Bitmap> bitmapList = new CopyOnWriteArrayList<>();        // Скорость?..
        // Чистим за собой
        // onComplete после unsubscribe не вызывается, но GC всё равно всё собирает
        final Action0 clearBitmaps = () -> {
            for (Bitmap bitmap : bitmapList) {
                bitmap.recycle();
            }
        };

        return Observable.from(urls)
                .groupBy(s -> counter.getAndIncrement() % NUMBER_OF_THREADS) // Делим на NUMBER_OF_THREADS потоков
                .flatMap(g -> g.subscribeOn(Schedulers.newThread()))         // Грузим картинки (вроде) параллельно
                .map(this::downloadImage)                                    // Превращаем String в Bitmap
                .filter(b -> b != null)                                      // Выкидываем незагрузившиеся
                .flatMap(new Func1<Bitmap, Observable<List<Bitmap>>>() {     // Превращаем Bitmap в List для CollageStrategy
                    @Override
                    public Observable<List<Bitmap>> call(Bitmap bitmap) {
                        bitmapList.add(bitmap);
                        return Observable.just(bitmapList);
                    }
                })
                .filter(list -> Math.round(Math.sqrt(list.size())) == Math.sqrt(list.size()))        // Будем делать коллаж каждые 1, 4, 9, 25, 36... картинок, дабы было квадратненько
                .sample(1, TimeUnit.SECONDS)
                .map(list -> list.subList(0, (int) Math.pow(Math.floor(Math.sqrt(list.size())), 2))) // См. ниже
                .flatMap(list -> Observable.just(collageStrategy.create(list)))                      // Собсно делаем коллаж
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onCreateCollage, Throwable::printStackTrace, clearBitmaps);               // Обработка ошибок напргяает
        // Проблема с sample (да и вообще со всем кодом выше) в том, что он не даёт Observable отправлять список битмапов чаще, чем раз в секунду
        // Однако до него есть оператор flatMap, который всё время что-то добавляет в этот список
        // В итоге коллаж делается не из 1, 4, 9... картинок, а из сколько выйдет
        // Поэтому приходится брать корень из list.size(), округлять его вниз и возводить в квадрат
        // В качестве выхода можно было бы отправлять в CollageStrategy Observable<Bitmap> и пусть он там сам обпроверяется, но интерфейсы трогать негоже
    }

    // Picasso кеширует картинки на диск, скажем ей спасибо
    private Bitmap downloadImage(String url) {
        try {
            return mPicasso.load(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}