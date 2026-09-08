package com.kipti.bnb.mixin;

import com.kipti.bnb.content.kinetics.cogwheel_carriage.contraption.CogwheelChainCarriageContraptionEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.protocol.Packet;

/**
 * Suppress position updates for contraption carriage entities since we want to do our own smarter position tracking specific to chains.
 *
 */
@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow
    @Final
    private Entity entity;

    @WrapOperation(method = "sendChanges", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/server/level/ServerEntity$Synchronizer;sendToTrackingPlayers(Lnet/minecraft/network/protocol/Packet;)V"))
    private void bits_n_bobs$suppressCarriagePositionSync(final ServerEntity.Synchronizer instance, final Packet<?> packet, final Operation<Void> original) {
        if (this.entity instanceof CogwheelChainCarriageContraptionEntity) {
            return;
        }
        original.call(instance, packet);
    }

}
