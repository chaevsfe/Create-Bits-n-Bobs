package com.kipti.bnb.network.packets.to_client;

import com.kipti.bnb.foundation.config.BnbServerSettings;
import com.kipti.bnb.network.BnbPackets;
import com.kipti.bnb.network.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ServerConfigPacket(BnbServerSettings.Values values) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerConfigPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                final BnbServerSettings.Values v = packet.values;
                buf.writeBoolean(v.flywheelStorageCapability());
                buf.writeFloat(v.flywheelStorageFactor());
                buf.writeFloat(v.flywheelTransferCapacityPerAngularMass());
                buf.writeFloat(v.flywheelMaxRpmFactor());
                buf.writeFloat(v.cogwheelChainDriveCostFactor());
                buf.writeVarInt(v.headlampCcBlockRange());
                buf.writeVarInt(v.cogwheelMaxBounds());
                buf.writeVarInt(v.cogwheelMaxNodeCount());
            },
            buf -> new ServerConfigPacket(new BnbServerSettings.Values(
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt()
            ))
    );

    public static ServerConfigPacket ofLocalConfig() {
        return new ServerConfigPacket(BnbServerSettings.local());
    }

    @Override
    public void handle(final LocalPlayer player) {
        BnbServerSettings.applySynced(this.values);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return BnbPackets.SERVER_CONFIG;
    }
}
