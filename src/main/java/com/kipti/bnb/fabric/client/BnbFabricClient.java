package com.kipti.bnb.fabric.client;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.CreateBitsnBobsClient;
import com.kipti.bnb.foundation.client.BnbClientHooks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class BnbFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BnbClientHooks.setBridge(new BnbClientBridge());
        BnbClientNetwork.register();
        CreateBitsnBobsClient.initClient();
        FabricLoader.getInstance().getModContainer(CreateBitsnBobs.MOD_ID).ifPresent(container ->
                ResourceLoader.registerBuiltinPack(CreateBitsnBobs.asResource("cogwheel_items"), container,
                        Component.literal("Material Cogwheel Items"), PackActivationType.ALWAYS_ENABLED));
    }

}
