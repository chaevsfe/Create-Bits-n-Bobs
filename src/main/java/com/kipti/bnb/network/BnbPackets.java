package com.kipti.bnb.network;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.network.packets.from_client.CogwheelChainCarriageQueueDisassemblyPacket;
import com.kipti.bnb.network.packets.from_client.CogwheelChainRidingPacket;
import com.kipti.bnb.network.packets.from_client.DragInteractionUpdatePacket;
import com.kipti.bnb.network.packets.from_client.PartialEditCogwheelChainPacket;
import com.kipti.bnb.network.packets.from_client.PlaceCogwheelChainPacket;
import com.kipti.bnb.network.packets.from_client.WrenchCogwheelChainPacket;
import com.kipti.bnb.network.packets.to_client.CogwheelChainCarriageUpdateDistPacket;
import com.kipti.bnb.network.packets.to_client.CogwheelChainRidingBroadcastPacket;
import com.kipti.bnb.network.packets.to_client.PeekCogwheelChainControllerHighlightPacket;
import com.kipti.bnb.network.packets.to_client.ServerConfigPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public enum BnbPackets implements BasePacketPayload.PacketTypeProvider {
    // C2S
    PLACE_COGWHEEL_CHAIN(PlaceCogwheelChainPacket.class, PlaceCogwheelChainPacket.STREAM_CODEC, true),
    WRENCH_COGWHEEL_CHAIN(WrenchCogwheelChainPacket.class, WrenchCogwheelChainPacket.STREAM_CODEC, true),
    PARTIAL_EDIT_COGWHEEL_CHAIN(PartialEditCogwheelChainPacket.class, PartialEditCogwheelChainPacket.STREAM_CODEC, true),
    COGWHEEL_CHAIN_CARRIAGE_QUEUE_DISASSEMBLE(CogwheelChainCarriageQueueDisassemblyPacket.class, CogwheelChainCarriageQueueDisassemblyPacket.STREAM_CODEC, true),
    COGWHEEL_CHAIN_RIDING(CogwheelChainRidingPacket.class, CogwheelChainRidingPacket.STREAM_CODEC, true),
    DRAG_INTERACTION_UPDATE(DragInteractionUpdatePacket.class, DragInteractionUpdatePacket.STREAM_CODEC, true),

    // S2C
    PEEK_COGWHEEL_CHAIN_CONTROLLER_HIGHLIGHT(PeekCogwheelChainControllerHighlightPacket.class, PeekCogwheelChainControllerHighlightPacket.STREAM_CODEC, false),
    COGWHEEL_CHAIN_CARRIAGE_UPDATE_DIST(CogwheelChainCarriageUpdateDistPacket.class, CogwheelChainCarriageUpdateDistPacket.STREAM_CODEC, false),
    COGWHEEL_CHAIN_RIDING_BROADCAST(CogwheelChainRidingBroadcastPacket.class, CogwheelChainRidingBroadcastPacket.STREAM_CODEC, false),
    SERVER_CONFIG(ServerConfigPacket.class, ServerConfigPacket.STREAM_CODEC, false),
    ;

    private final BnbPacketType<?> packetType;
    private final boolean serverbound;

    <T extends BasePacketPayload> BnbPackets(final Class<T> clazz,
                                             final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                             final boolean serverbound) {
        final String name = this.name().toLowerCase(Locale.ROOT);
        this.packetType = new BnbPacketType<>(new CustomPacketPayload.Type<>(CreateBitsnBobs.asResource(name)), clazz, codec);
        this.serverbound = serverbound;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.packetType.type();
    }

    public boolean isServerbound() {
        return serverbound;
    }

    public static void register() {
        for (final BnbPackets packet : values())
            packet.registerOne();
    }

    @SuppressWarnings("unchecked")
    private <T extends BasePacketPayload> void registerOne() {
        final BnbPacketType<T> type = (BnbPacketType<T>) this.packetType;
        if (serverbound) {
            PayloadTypeRegistry.serverboundPlay().register(type.type(), (StreamCodec<RegistryFriendlyByteBuf, T>) type.codec());
            ServerPlayNetworking.registerGlobalReceiver(type.type(), (payload, context) -> {
                final ServerPlayer player = context.player();
                context.server().execute(() -> ((ServerboundPacketPayload) payload).handle(player));
            });
        } else {
            PayloadTypeRegistry.clientboundPlay().register(type.type(), (StreamCodec<RegistryFriendlyByteBuf, T>) type.codec());
        }
    }
}
