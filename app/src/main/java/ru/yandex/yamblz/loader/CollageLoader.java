package ru.yandex.yamblz.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.util.List;

/**
 * Creates collages from images loaded from the given urls
 */
public interface CollageLoader {

    /**
     * Loads images from urls, collages them and sets to imageView. Uses default collage strategy
     * @param urls urls to load
     * @param imageView imageView to set image to
     * @return subscription for job
     */
    Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView);

    /**
     * Loads images from urls, collages them and callbacks the collage to imageTarget. Uses default
     * collage strategy
     * @param urls urls to load
     * @param imageTarget callback
     * @return subscription for job
     */
    Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget);

    /**
     * Loads images from urls, collages them using the given collage strategy and sets to imageView
     * @param urls urls to load
     * @param imageView imageView to set image to
     * @param collageStrategy collage strategy to use
     * @return subscription for job
     */
    Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageView imageView,
                     @Nullable CollageStrategy collageStrategy);

    /**
     * Loads images from urls, collages them using the given collage strategy and callbacks the
     * collage to imageTarget.
     * @param urls urls to load
     * @param imageTarget callback
     * @param collageStrategy collage strategy to use
     * @return subscription for job
     */
    Subscription loadCollage(@NonNull List<String> urls, @NonNull ImageTarget imageTarget,
                     @Nullable CollageStrategy collageStrategy);

}
