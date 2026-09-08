package com.kipti.bnb.registry.content;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.content.kinetics.cogwheel_carriage.contraption.CogwheelChainCarriageContraption;
import com.zurrtum.create.api.contraption.ContraptionType;
import com.zurrtum.create.api.registry.CreateRegistries;
import com.zurrtum.create.content.contraptions.Contraption;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public class BnbContraptionTypes {

    public static final ContraptionType COGWHEEL_CHAIN_CARRIAGE = register("cogwheel_chain_carriage", CogwheelChainCarriageContraption::new);

    private static ContraptionType register(final String name, final Supplier<? extends Contraption> factory) {
        return Registry.register(CreateRegistries.CONTRAPTION_TYPE, CreateBitsnBobs.asResource(name), new ContraptionType(factory));
    }

    public static void register() {
        if (!CreateRegistries.CONTRAPTION_TYPE.containsKey(CreateBitsnBobs.asResource("cogwheel_chain_carriage")))
            throw new IllegalStateException("Bits 'n' Bobs contraption types did not register");
    }

}
