package com.kipti.bnb.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface BasePacketPayload extends CustomPacketPayload {

    PacketTypeProvider getTypeProvider();

    @Override
    default Type<? extends CustomPacketPayload> type() {
        return getTypeProvider().getType();
    }

    interface PacketTypeProvider {
        <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType();
    }
}
