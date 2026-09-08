package com.kipti.bnb.fabric.client;

import com.kipti.bnb.foundation.config.BnbServerSettings;
import com.kipti.bnb.network.BnbPackets;
import com.kipti.bnb.network.ClientboundPacketPayload;
import com.kipti.bnb.network.packets.to_client.ServerConfigPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class BnbClientNetwork {

    private BnbClientNetwork() {
    }

    public static void register() {
        for (final BnbPackets packet : BnbPackets.values()) {
            if (packet.isServerbound())
                continue;
            if (packet == BnbPackets.SERVER_CONFIG)
                registerServerConfig();
            else
                registerOne(packet.getType());
        }
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> BnbServerSettings.clearSynced());
    }

    private static void registerServerConfig() {
        ClientPlayNetworking.registerGlobalReceiver(
                BnbPackets.SERVER_CONFIG.<ServerConfigPacket>getType(),
                (payload, context) -> context.client().execute(() -> {
                    if (context.client().hasSingleplayerServer() || context.player() == null)
                        return;
                    payload.handle(context.player());
                })
        );
    }

    private static <T extends CustomPacketPayload> void registerOne(final CustomPacketPayload.Type<T> type) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            final ClientboundPacketPayload clientbound = (ClientboundPacketPayload) payload;
            context.client().execute(() -> {
                if (context.player() == null)
                    return;
                clientbound.handle(context.player());
            });
        });
    }

}
