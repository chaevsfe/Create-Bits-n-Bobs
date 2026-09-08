package com.kipti.bnb.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record BnbPacketType<T extends BasePacketPayload>(
        CustomPacketPayload.Type<T> type,
        Class<T> clazz,
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec
) {
}
