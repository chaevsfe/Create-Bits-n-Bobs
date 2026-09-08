package com.kipti.bnb.fabric.client;

import com.kipti.bnb.foundation.client.BnbClientHooks;
import com.kipti.bnb.network.ServerboundPacketPayload;
import com.zurrtum.create.client.flywheel.lib.visualization.VisualizationHelper;
import com.zurrtum.create.client.foundation.utility.ServerSpeedProvider;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class BnbClientBridge implements BnbClientHooks.Bridge {

    @Override
    public void queueVisualUpdate(final BlockEntity blockEntity) {
        VisualizationHelper.queueUpdate(blockEntity);
    }

    @Override
    public void sendToServer(final ServerboundPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public float serverSpeedModifier() {
        return ServerSpeedProvider.get();
    }

}
