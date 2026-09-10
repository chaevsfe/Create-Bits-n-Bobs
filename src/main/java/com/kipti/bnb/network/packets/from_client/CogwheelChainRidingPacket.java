package com.kipti.bnb.network.packets.from_client;

import com.kipti.bnb.content.kinetics.cogwheel_chain.behaviour.CogwheelChainBehaviour;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.PlacingCogwheelChain;
import com.kipti.bnb.content.kinetics.cogwheel_chain.riding.ServerCogwheelChainRidingHandler;
import com.kipti.bnb.foundation.behaviour.SuperBlockEntityBehaviour;
import com.kipti.bnb.mixin.ServerGamePacketListenerImplAccessor;
import com.kipti.bnb.network.BnbPackets;
import com.kipti.bnb.network.ServerboundPacketPayload;
import com.zurrtum.create.AllItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record CogwheelChainRidingPacket(
        BlockPos controllerPos,
        boolean stop
) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, CogwheelChainRidingPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CogwheelChainRidingPacket::controllerPos,
                    ByteBufCodecs.BOOL, CogwheelChainRidingPacket::stop,
                    CogwheelChainRidingPacket::new
            );

    @Override
    public void handle(final ServerPlayer player) {
        if (this.stop) {
            ServerCogwheelChainRidingHandler.handleStopRidingPacket(player);
            return;
        }

        if (player.isSpectator())
            return;

        if (!player.isHolding(stack -> stack.is(AllItemTags.CHAIN_RIDEABLE)))
            return;

        final ServerLevel level = player.level();
        if (!level.isLoaded(this.controllerPos))
            return;

        if (player.distanceToSqr(
                this.controllerPos.getX() + 0.5,
                this.controllerPos.getY() + 0.5,
                this.controllerPos.getZ() + 0.5
        ) > PlacingCogwheelChain.getCogwheelMaxInteractionDistanceSq())
            return;

        final CogwheelChainBehaviour behaviour = SuperBlockEntityBehaviour.get(
                level,
                this.controllerPos,
                CogwheelChainBehaviour.TYPE
        );
        if (behaviour == null || !behaviour.isController() || behaviour.getControlledChain() == null)
            return;

        player.fallDistance = 0;
        ((ServerGamePacketListenerImplAccessor) player.connection).bits_n_bobs$setAboveGroundTickCount(0);
        ((ServerGamePacketListenerImplAccessor) player.connection).bits_n_bobs$setAboveGroundVehicleTickCount(0);
        ServerCogwheelChainRidingHandler.handleTTLPacket(player);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return BnbPackets.COGWHEEL_CHAIN_RIDING;
    }
}
