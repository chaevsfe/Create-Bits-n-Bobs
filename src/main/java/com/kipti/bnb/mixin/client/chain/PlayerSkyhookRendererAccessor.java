package com.kipti.bnb.mixin.client.chain;

import com.zurrtum.create.client.foundation.render.PlayerSkyhookRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

@Mixin(PlayerSkyhookRenderer.class)
public interface PlayerSkyhookRendererAccessor {

    @Accessor("hangingPlayers")
    static Set<UUID> bits_n_bobs$getHangingPlayers() {
        throw new AssertionError();
    }
}
