package ru.yandex.yamblz.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.util.List;

public interface CollageLoader {

    void loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView);

    void loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView, @Nullable String tag);

    void loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget);

    void loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget, @Nullable String tag);

    void loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView,
                     @Nullable CollageStrategy collageStrategy);

    void loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView, @Nullable String tag,
                     @Nullable CollageStrategy collageStrategy);

    void loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget,
                     @Nullable CollageStrategy collageStrategy);

    void loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget, @Nullable String tag,
                     @Nullable CollageStrategy collageStrategy);

}
