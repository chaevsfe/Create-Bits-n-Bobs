package com.kipti.bnb.content.kinetics.cogwheel_chain.riding;

import com.kipti.bnb.mixin.client.chain.PlayerSkyhookRendererAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Keeps BnB hanging players merged into Create's shared skyhook renderer state.
 */
@Environment(EnvType.CLIENT)
public final class PlayerSkyhookRendererBridge {

    private static final Set<UUID> bnbHangingPlayers = new HashSet<>();

    private PlayerSkyhookRendererBridge() {
    }

    public static void updateBnbHangingPlayers(final Collection<UUID> uuids) {
        final Set<UUID> hangingPlayers = PlayerSkyhookRendererAccessor.bits_n_bobs$getHangingPlayers();
        hangingPlayers.removeAll(bnbHangingPlayers);
        bnbHangingPlayers.clear();
        bnbHangingPlayers.addAll(uuids);
        hangingPlayers.addAll(bnbHangingPlayers);
    }

    public static void clear() {
        PlayerSkyhookRendererAccessor.bits_n_bobs$getHangingPlayers().removeAll(bnbHangingPlayers);
        bnbHangingPlayers.clear();
    }

    public static void restoreBnbHangingPlayers() {
        PlayerSkyhookRendererAccessor.bits_n_bobs$getHangingPlayers().addAll(bnbHangingPlayers);
    }
}
