package com.kipti.bnb.mixin;

import com.kipti.bnb.foundation.behaviour.extensions.KineticBehaviourExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.RotationPropagator;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(RotationPropagator.class)
public class RotationPropagatorBehaviourMixin {

    @WrapOperation(method = "getPotentialNeighbourLocations", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/base/KineticBlockEntity;addPropagationLocations(Lcom/zurrtum/create/content/kinetics/base/IRotate;Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/List;)Ljava/util/List;"))
    private static List<BlockPos> bits_n_bobs$addBehaviourPropagationLocations(final KineticBlockEntity instance,
                                                                               final IRotate block,
                                                                               final BlockState state,
                                                                               final List<BlockPos> neighbours,
                                                                               final Operation<List<BlockPos>> original) {
        List<BlockPos> locations = original.call(instance, block, state, neighbours);
        if (instance instanceof final SmartBlockEntity smartBlockEntity)
            for (final BlockEntityBehaviour<?> behaviour : smartBlockEntity.getAllBehaviours())
                if (behaviour instanceof final KineticBehaviourExtension extension)
                    locations = extension.addExtraPropagationLocations(block, state, locations);
        return locations;
    }

    @WrapOperation(method = "getRotationSpeedModifier", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/base/KineticBlockEntity;propagateRotationTo(Lcom/zurrtum/create/content/kinetics/base/KineticBlockEntity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;ZZ)F"))
    private static float bits_n_bobs$propagateBehaviourRotation(final KineticBlockEntity instance,
                                                                final KineticBlockEntity target,
                                                                final BlockState stateFrom,
                                                                final BlockState stateTo,
                                                                final BlockPos diff,
                                                                final boolean connectedViaAxes,
                                                                final boolean connectedViaCogs,
                                                                final Operation<Float> original) {
        final float originalValue = original.call(instance, target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
        if (originalValue != 0 || !(instance instanceof final SmartBlockEntity smartBlockEntity))
            return originalValue;
        for (final BlockEntityBehaviour<?> behaviour : smartBlockEntity.getAllBehaviours())
            if (behaviour instanceof final KineticBehaviourExtension extension) {
                final float propagated = extension.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
                if (propagated != 0)
                    return propagated;
            }
        return originalValue;
    }
}
