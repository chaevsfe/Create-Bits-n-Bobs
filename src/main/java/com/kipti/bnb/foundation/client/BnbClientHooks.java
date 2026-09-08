package com.kipti.bnb.foundation.client;

import com.kipti.bnb.network.ServerboundPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Server-safe entry point for the few things only a client can do. The client entrypoint installs a
 * {@link Bridge}; on a dedicated server every call is a no-op and no client class is ever resolved.
 */
public final class BnbClientHooks {

    public interface Bridge {
        void queueVisualUpdate(BlockEntity blockEntity);

        void sendToServer(ServerboundPacketPayload payload);

        float serverSpeedModifier();
    }

    @Nullable
    private static Bridge bridge;

    private BnbClientHooks() {
    }

    public static void setBridge(final Bridge clientBridge) {
        bridge = clientBridge;
    }

    public static void queueVisualUpdate(final BlockEntity blockEntity) {
        if (bridge != null)
            bridge.queueVisualUpdate(blockEntity);
    }

    public static void sendToServer(final ServerboundPacketPayload payload) {
        if (bridge != null)
            bridge.sendToServer(payload);
    }

    public static float serverSpeedModifier() {
        return bridge == null ? 1 : bridge.serverSpeedModifier();
    }
}
