package com.kipti.bnb.mixin.client.chain;

import com.kipti.bnb.content.kinetics.cogwheel_chain.riding.CogwheelChainRidingHelper;
import com.zurrtum.create.client.content.kinetics.chainConveyor.ChainConveyorRidingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChainConveyorRidingHandler.class)
public class ChainConveyorRidingHandlerMixin {

    @Inject(method = "embark", at = @At("HEAD"))
    private static void bits_n_bobs$disembarkPreviousRide(final Minecraft minecraft, final BlockPos lift,
                                                          final float position, final BlockPos connection,
                                                          final CallbackInfo ci) {
        CogwheelChainRidingHelper.disembarkFromAnyPreviousRide();
    }
}
