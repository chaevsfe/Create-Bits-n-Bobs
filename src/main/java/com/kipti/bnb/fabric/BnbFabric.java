package com.kipti.bnb.fabric;

import com.kipti.bnb.CreateBitsnBobs;
import net.fabricmc.api.ModInitializer;

public final class BnbFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BnbCreatePlugin.verifyEarlyRegistrationComplete();
        CreateBitsnBobs.init();
    }
}
