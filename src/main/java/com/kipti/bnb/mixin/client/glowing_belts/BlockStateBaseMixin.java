package com.kipti.bnb.mixin.client.glowing_belts;

import com.kipti.bnb.foundation.BnbBlockStateProperties;
import com.zurrtum.create.AllBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {

    @Inject(method = "emissiveRendering", at = @At("HEAD"), cancellable = true)
    private void bits_n_bobs$glowingBeltIsEmissive(final CallbackInfoReturnable<Boolean> cir) {
        final BlockState state = (BlockState) (Object) this;
        if (state.getBlock() != AllBlocks.BELT)
            return;
        if (state.hasProperty(BnbBlockStateProperties.GLOWING) && state.getValue(BnbBlockStateProperties.GLOWING))
            cir.setReturnValue(true);
    }
}
