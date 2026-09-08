package com.kipti.bnb.network;

import net.minecraft.server.level.ServerPlayer;

public interface ServerboundPacketPayload extends BasePacketPayload {

    void handle(ServerPlayer player);
}
