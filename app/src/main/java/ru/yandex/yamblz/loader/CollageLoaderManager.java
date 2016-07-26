package ru.yandex.yamblz.loader;

public class CollageLoaderManager {

    private CollageLoaderManager() {
    }

    private static CollageLoader sCollageLoader;

    public static void init(CollageLoader collageLoader) {
        sCollageLoader = collageLoader;
    }

    public static CollageLoader getLoader() {
        if (sCollageLoader == null) {
            sCollageLoader = new CollageLoaderImpl();
        }
        return sCollageLoader;
    }
}
