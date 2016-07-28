package ru.yandex.yamblz.handler;

import android.os.Handler;
import android.os.Looper;

public class CriticalSectionsManager {

    private static volatile CriticalSectionsHandler sCriticalSectionsHandler;

    private CriticalSectionsManager() {
    }

    public static void init(CriticalSectionsHandler criticalSectionsHandler) {
        sCriticalSectionsHandler = criticalSectionsHandler;
    }

    public static CriticalSectionsHandler getHandler() {
        if (sCriticalSectionsHandler == null) {
            sCriticalSectionsHandler = new StubCriticalSectionsHandler(new Handler(Looper.getMainLooper()));
        }
        return sCriticalSectionsHandler;
    }
}
