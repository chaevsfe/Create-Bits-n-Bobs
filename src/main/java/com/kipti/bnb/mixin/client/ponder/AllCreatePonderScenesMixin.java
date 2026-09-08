package com.kipti.bnb.mixin.client.ponder;

import com.kipti.bnb.foundation.ponder.create.BnbCreatePonderScenes;
import com.zurrtum.create.client.infrastructure.ponder.AllCreatePonderScenes;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AllCreatePonderScenes.class)
public class AllCreatePonderScenesMixin {

    @Inject(method = "register", at = @At("TAIL"))
    private static void bits_n_bobs$registerScenes(final PonderSceneRegistrationHelper<Identifier> helper, final CallbackInfo ci) {
        BnbCreatePonderScenes.register(helper);
    }

}
