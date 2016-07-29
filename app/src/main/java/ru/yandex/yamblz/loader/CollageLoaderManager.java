package ru.yandex.yamblz.loader;

import ru.yandex.yamblz.loader.interfaces.CollageLoader;

public class CollageLoaderManager
{
    private static CollageLoader sCollageLoader;

    public static void init(CollageLoader collageLoader)
    {
        sCollageLoader = collageLoader;
    }

    public static CollageLoader getLoader()
    {
        if (sCollageLoader == null)
        {
            sCollageLoader = new StubCollageLoader();
        }

        return sCollageLoader;
    }

    public static void destroy()
    {
        sCollageLoader = null;
    }
}
