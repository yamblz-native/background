package ru.yandex.yamblz.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.util.List;

public interface CollageLoader {

    Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView);

    Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget);

    Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView,
                     @Nullable CollageStrategy collageStrategy);

    Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget,
                     @Nullable CollageStrategy collageStrategy);

}
