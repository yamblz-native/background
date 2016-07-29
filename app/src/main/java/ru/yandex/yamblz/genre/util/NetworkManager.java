package ru.yandex.yamblz.genre.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by platon on 29.07.2016.
 */
public class NetworkManager
{
    private static NetworkManager sInstance;
    private Context mContext;

    public static void init(Context context)
    {
        sInstance = new NetworkManager(context);
    }

    public static NetworkManager getManager()
    {
        return sInstance;
    }

    private NetworkManager(Context context)
    {
        mContext = context;
    }

    public boolean networkIsAvailable()
    {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        return  (activeInfo != null && activeInfo.isConnected());
    }
}
