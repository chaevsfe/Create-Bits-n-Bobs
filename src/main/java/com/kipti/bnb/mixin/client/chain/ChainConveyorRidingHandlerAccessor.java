package com.kipti.bnb.mixin.client.chain;

import com.zurrtum.create.client.content.kinetics.chainConveyor.ChainConveyorRidingHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChainConveyorRidingHandler.class)
public interface ChainConveyorRidingHandlerAccessor {

    @Invoker("stopRiding")
    static void bits_n_bobs$invokeStopRiding(final Minecraft minecraft) {
        throw new AssertionError();
    }
}
