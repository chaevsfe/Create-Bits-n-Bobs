package com.kipti.bnb;

import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChainCandidate;
import com.kipti.bnb.content.kinetics.cogwheel_chain.riding.ServerCogwheelChainRidingHandler;
import com.kipti.bnb.foundation.caching.LevelSafeStorage;
import com.kipti.bnb.foundation.caching.LevelSimpleCache;
import com.kipti.bnb.foundation.command.BnbCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import com.kipti.bnb.network.BnbNetwork;
import com.kipti.bnb.network.packets.to_client.ServerConfigPacket;

public final class CommonEvents {

    private CommonEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> ServerCogwheelChainRidingHandler.tick());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ServerCogwheelChainRidingHandler.reset());
        ServerLevelEvents.UNLOAD.register((server, level) -> {
            LevelSimpleCache.clearAllForLevel(level);
            LevelSafeStorage.clearAllForLevel(level);
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> BnbNetwork.sendToClient(handler.getPlayer(), ServerConfigPacket.ofLocalConfig()));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, access, success) -> CogwheelChainCandidate.clearCache());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> BnbCommands.register(dispatcher));
    }
}
