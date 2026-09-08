package com.kipti.bnb.mixin.client.model;

import com.kipti.bnb.foundation.client.BnbModels;
import com.zurrtum.create.client.AllModels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the port's model wrappers after Create Fly has registered its own, so a wrapper composes onto Create's
 * instead of replacing it. Doing this from the client entrypoint would race Fly's own initialiser.
 */
@Environment(EnvType.CLIENT)
@Mixin(AllModels.class)
public abstract class AllModelsMixin {

    @Inject(method = "register()V", at = @At("TAIL"))
    private static void bnb$registerModels(CallbackInfo ci) {
        BnbModels.register();
    }
}
