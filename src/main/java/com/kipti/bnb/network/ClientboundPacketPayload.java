package com.kipti.bnb.network;

import net.minecraft.client.player.LocalPlayer;

public interface ClientboundPacketPayload extends BasePacketPayload {

    void handle(LocalPlayer player);
}
