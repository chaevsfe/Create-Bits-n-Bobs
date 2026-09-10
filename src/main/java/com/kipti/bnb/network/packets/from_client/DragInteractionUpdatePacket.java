package com.kipti.bnb.network.packets.from_client;

import com.kipti.bnb.foundation.behaviour.drag.DragInteractionBehaviour;
import com.kipti.bnb.network.BnbPackets;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.kipti.bnb.network.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public record DragInteractionUpdatePacket(
        BlockPos pos,
        int value
) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, DragInteractionUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, DragInteractionUpdatePacket::pos,
                    ByteBufCodecs.VAR_INT, DragInteractionUpdatePacket::value,
                    DragInteractionUpdatePacket::new
            );

    @Override
    public void handle(ServerPlayer player) {
        if (player.distanceToSqr(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) > 100) {
            return;
        }

        if (player.isSpectator() || !player.mayBuild()) {
            return;
        }

        ServerLevel level = player.level();
        if (!level.isLoaded(this.pos) || !level.mayInteract(player, this.pos)) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(this.pos);
        if (!(blockEntity instanceof SmartBlockEntity sbe)) {
            return;
        }

        DragInteractionBehaviour behaviour = sbe.getBehaviour(DragInteractionBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.updateTargetValue(this.value);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return BnbPackets.DRAG_INTERACTION_UPDATE;
    }
}
