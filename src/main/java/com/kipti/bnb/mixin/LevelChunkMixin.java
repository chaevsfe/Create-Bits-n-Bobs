package com.kipti.bnb.mixin;

import com.kipti.bnb.foundation.behaviour.SuperBlockEntityBehaviour;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

    @Shadow
    @Final
    Level level;

    @WrapOperation(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
    private void bits_n_bobs$removeSuperBehavioursFromLevel(final LevelChunk instance,
                                                            final BlockPos pos,
                                                            final Operation<Void> original,
                                                            @Local(argsOnly = true) final int flags) {
        if (this.level.getBlockEntity(pos) instanceof final SmartBlockEntity smartBlockEntity) {
            final boolean isMoving = (flags & Block.UPDATE_MOVE_BY_PISTON) != 0;
            for (final BlockEntityBehaviour<?> behaviour : smartBlockEntity.getAllBehaviours())
                if (behaviour instanceof final SuperBlockEntityBehaviour superBehaviour)
                    superBehaviour.removeFromLevel(isMoving);
        }
        original.call(instance, pos);
    }
}
