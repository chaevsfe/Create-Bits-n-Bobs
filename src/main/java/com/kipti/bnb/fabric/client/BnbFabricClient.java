package com.kipti.bnb.fabric.client;

import com.kipti.bnb.CreateBitsnBobsClient;
import com.kipti.bnb.foundation.client.BnbClientHooks;
import net.fabricmc.api.ClientModInitializer;

public class BnbFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BnbClientHooks.setBridge(new BnbClientBridge());
        BnbClientNetwork.register();
        CreateBitsnBobsClient.initClient();
    }

}
