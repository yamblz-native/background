package ru.yandex.yamblz.handler;

public class CriticalSectionsManager {

    private static CriticalSectionsHandler sCriticalSectionsHandler;

    public static void init(CriticalSectionsHandler criticalSectionsHandler) {
        sCriticalSectionsHandler = criticalSectionsHandler;
    }

    public static CriticalSectionsHandler getHandler() {
        if (sCriticalSectionsHandler == null) {
            sCriticalSectionsHandler = new StubCriticalSectionsHandler();
        }
        return sCriticalSectionsHandler;
    }
}
