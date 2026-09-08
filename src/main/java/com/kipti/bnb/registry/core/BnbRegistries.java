package com.kipti.bnb.registry.core;

import com.kipti.bnb.content.kinetics.cogwheel_chain.types.CogwheelChainType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;

public class BnbRegistries {

    public static final Registry<CogwheelChainType> COGWHEEL_CHAIN_TYPES =
            FabricRegistryBuilder.create(BnbResourceKeys.COGWHEEL_CHAIN_TYPE).buildAndRegister();

}
