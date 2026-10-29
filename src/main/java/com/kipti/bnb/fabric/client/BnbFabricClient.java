package com.kipti.bnb.fabric.client;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.CreateBitsnBobsClient;
import com.kipti.bnb.foundation.client.BnbClientHooks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.network.chat.Component;

public class BnbFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BnbClientHooks.setBridge(new BnbClientBridge());
        BnbClientNetwork.register();
        CreateBitsnBobsClient.initClient();
        FabricLoader.getInstance().getModContainer(CreateBitsnBobs.MOD_ID).ifPresent(container -> {
            ResourceLoader.registerBuiltinPack(CreateBitsnBobs.asResource("cogwheel_items"), container,
                    Component.literal("Material Cogwheel Items"), PackActivationType.ALWAYS_ENABLED);
            if (installedCreateMatches(">=6.0.9-2"))
                ResourceLoader.registerBuiltinPack(CreateBitsnBobs.asResource("fly_6092_encased_shaft"), container,
                        Component.literal("Iron Encased Shafts for Create Fly 6.0.9-2"), PackActivationType.ALWAYS_ENABLED);
        });
    }

    private static boolean installedCreateMatches(String range) {
        VersionPredicate predicate;
        try {
            predicate = VersionPredicate.parse(range);
        } catch (VersionParsingException e) {
            return false;
        }
        return FabricLoader.getInstance().getModContainer("create")
                .map(ModContainer::getMetadata)
                .map(metadata -> predicate.test(metadata.getVersion()))
                .orElse(false);
    }

}
