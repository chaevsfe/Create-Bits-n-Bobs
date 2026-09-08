package com.kipti.bnb.fabric;

import com.kipti.bnb.CreateBitsnBobs;
import com.zurrtum.create.api.registry.CreateRegisterPlugin;

public final class BnbCreatePlugin implements CreateRegisterPlugin {
    private static boolean blocksRegistered;

    @Override
    public void onBlockRegister() {
        if (blocksRegistered) {
            throw new IllegalStateException("Create Fly invoked Bits 'n' Bobs block registration more than once");
        }
        CreateBitsnBobs.registerBlocksEarly();
        blocksRegistered = true;
    }

    public static void verifyEarlyRegistrationComplete() {
        if (!blocksRegistered) {
            throw new IllegalStateException("Create Fly did not invoke Bits 'n' Bobs early block registration");
        }
    }
}
