package com.kipti.bnb.mixin;

import com.kipti.bnb.foundation.behaviour.SuperBlockEntityBehaviour;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.contraptions.StructureTransform;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureTransform.class)
public class StructureTransformMixin {

    @Inject(method = "apply(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("HEAD"))
    private void bits_n_bobs$transformSuperBehaviours(final BlockEntity blockEntity, final CallbackInfo ci) {
        if (!(blockEntity instanceof final SmartBlockEntity smartBlockEntity))
            return;
        for (final BlockEntityBehaviour<?> behaviour : smartBlockEntity.getAllBehaviours())
            if (behaviour instanceof final SuperBlockEntityBehaviour superBehaviour)
                superBehaviour.transform(blockEntity, (StructureTransform) (Object) this);
    }
}
