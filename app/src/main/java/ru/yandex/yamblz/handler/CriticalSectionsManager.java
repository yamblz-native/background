package ru.yandex.yamblz.handler;

import android.os.Handler;

import static android.os.Looper.getMainLooper;


public class CriticalSectionsManager {

    private static CriticalSectionsHandler sCriticalSectionsHandler;
    private static Handler handler;

    public static void init(CriticalSectionsHandler criticalSectionsHandler) {
        handler = new Handler(getMainLooper());
        sCriticalSectionsHandler = criticalSectionsHandler;
    }

    public static CriticalSectionsHandler getHandler() {
        if (sCriticalSectionsHandler == null) {
            sCriticalSectionsHandler =
                    new StubCriticalSectionsHandler((task -> handler.post(task::run)));
        }
        return sCriticalSectionsHandler;
    }
}
