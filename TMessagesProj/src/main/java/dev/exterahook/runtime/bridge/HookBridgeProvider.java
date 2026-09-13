package dev.exterahook.runtime.bridge;

import dev.exterahook.runtime.internal.HookClassA;
import dev.exterahook.runtime.internal.HookClassB;
import dev.exterahook.runtime.internal.HookClassC;
import c.g;

public final class HookBridgeProvider {

    private static volatile boolean useEmbeddedByDefault;
    private static HookBridge defaultBridge;
    private static HookBridge embeddedBridge;

    private HookBridgeProvider() {
    }

    private static synchronized HookBridge getDefaultBridge() {
        if (defaultBridge == null) {
            g gVar = new g("exterahook", false);
            defaultBridge = new HookBridgeImpl(gVar, new HookClassB(), new HookClassA(new HookClassB(), new HookClassC(gVar)));
        }
        return defaultBridge;
    }

    private static synchronized HookBridge getEmbeddedBridge() {
        if (embeddedBridge == null) {
            g gVar = new g(null, true);
            embeddedBridge = new HookBridgeImpl(gVar, new HookClassB(), new HookClassA(new HookClassB(), new HookClassC(gVar)));
        }
        return embeddedBridge;
    }

    public static HookBridge createDefault() {
        return useEmbeddedByDefault ? getEmbeddedBridge() : getDefaultBridge();
    }

    public static void initializeDefault() {
        ((HookBridgeImpl) createDefault()).a();
        JniBridgeBindings.initialize0();
    }

    public static void useEmbeddedAsDefault() {
        useEmbeddedByDefault = true;
    }
}
