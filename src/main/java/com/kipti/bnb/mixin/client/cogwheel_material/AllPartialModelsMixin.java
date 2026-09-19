package com.kipti.bnb.mixin.client.cogwheel_material;

import com.kipti.bnb.registry.client.BnbPartialModels;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AllPartialModels.class, remap = false)
public class AllPartialModelsMixin {

    @Mutable
    @Shadow
    @Final
    public static PartialModel COGWHEEL;

    @Mutable
    @Shadow
    @Final
    public static PartialModel SHAFTLESS_COGWHEEL;

    @Mutable
    @Shadow
    @Final
    public static PartialModel SHAFTLESS_LARGE_COGWHEEL;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void bnb$materialCogwheelPartials(final CallbackInfo ci) {
        COGWHEEL = BnbPartialModels.MATERIAL_COGWHEEL;
        SHAFTLESS_COGWHEEL = BnbPartialModels.MATERIAL_COGWHEEL_SHAFTLESS;
        SHAFTLESS_LARGE_COGWHEEL = BnbPartialModels.MATERIAL_LARGE_COGWHEEL_SHAFTLESS;
    }
}
