package com.kipti.bnb.mixin.client.decoration;

import com.kipti.bnb.content.decoration.weathered_girder.WeatheredGirderBlock;
import com.zurrtum.create.client.content.decoration.girder.GirderCTBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(GirderCTBehaviour.class)
public class GirderCTBehaviourMixin {

    @Inject(method = "connectsTo", at = @At("RETURN"), cancellable = true)
    private void bnb$connectsToAdditionalGirderTypes(final BlockState state, final BlockState other, final BlockAndTintGetter reader, final BlockPos pos, final BlockPos otherPos, final Direction face, final CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue())
            return;
        if (other.getBlock() != state.getBlock() && !(other.getBlock() instanceof WeatheredGirderBlock))
            return;
        cir.setReturnValue(true);
    }

}

