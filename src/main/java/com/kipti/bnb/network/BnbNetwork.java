package com.kipti.bnb.network;

import com.kipti.bnb.foundation.client.BnbClientHooks;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class BnbNetwork {

    private BnbNetwork() {
    }

    public static void sendToClient(final ServerPlayer player, final ClientboundPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToClientsTrackingEntity(final Entity entity, final ClientboundPacketPayload payload) {
        for (final ServerPlayer player : PlayerLookup.tracking(entity))
            ServerPlayNetworking.send(player, payload);
    }

    public static void sendToAllClients(final MinecraftServer server, final ClientboundPacketPayload payload) {
        for (final ServerPlayer player : PlayerLookup.all(server))
            ServerPlayNetworking.send(player, payload);
    }

    public static void sendToServer(final ServerboundPacketPayload payload) {
        BnbClientHooks.sendToServer(payload);
    }
}
