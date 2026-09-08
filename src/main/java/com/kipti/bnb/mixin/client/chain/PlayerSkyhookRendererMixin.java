package com.kipti.bnb.mixin.client.chain;

import com.kipti.bnb.content.kinetics.cogwheel_chain.riding.PlayerSkyhookRendererBridge;
import com.zurrtum.create.client.foundation.render.PlayerSkyhookRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.UUID;

@Mixin(PlayerSkyhookRenderer.class)
public class PlayerSkyhookRendererMixin {

    @Inject(method = "updatePlayerList", at = @At("TAIL"))
    private static void bits_n_bobs$restoreBnbHangingPlayers(final Collection<UUID> uuids, final CallbackInfo ci) {
        PlayerSkyhookRendererBridge.restoreBnbHangingPlayers();
    }
}
