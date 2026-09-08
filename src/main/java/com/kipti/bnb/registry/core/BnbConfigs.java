package com.kipti.bnb.registry.core;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.foundation.config.BnbCommonConfig;
import com.kipti.bnb.foundation.config.BnbServerConfig;
import com.zurrtum.create.catnip.config.Builder;

public class BnbConfigs {

    private static BnbCommonConfig common;
    private static BnbServerConfig server;

    public static BnbCommonConfig common() {
        return common;
    }

    public static BnbServerConfig server() {
        return server;
    }

    public static void register() {
        common = Builder.create(BnbCommonConfig::new, CreateBitsnBobs.MOD_ID, "common", true);
        server = Builder.create(BnbServerConfig::new, CreateBitsnBobs.MOD_ID, "server", true);
        common.onLoad();
    }

}
