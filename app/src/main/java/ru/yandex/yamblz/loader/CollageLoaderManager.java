package ru.yandex.yamblz.loader;

public class CollageLoaderManager {

    private static volatile CollageLoader sCollageLoader;

    private CollageLoaderManager() {
    }

    public static void init(CollageLoader collageLoader) {
        sCollageLoader = collageLoader;
    }

    public static CollageLoader getLoader() {
        if (sCollageLoader == null) {
            sCollageLoader = new StubCollageLoader();
        }
        return sCollageLoader;
    }
}
