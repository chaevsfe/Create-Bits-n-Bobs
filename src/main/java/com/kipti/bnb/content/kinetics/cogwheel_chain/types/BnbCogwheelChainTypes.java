package com.kipti.bnb.content.kinetics.cogwheel_chain.types;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.registry.core.BnbRegistries;
import net.minecraft.core.Registry;
import com.kipti.bnb.registry.core.BnbTags;
import com.zurrtum.create.AllItems;
import net.minecraft.world.level.block.Blocks;
import com.kipti.bnb.foundation.data.CommonTags;

public class BnbCogwheelChainTypes {


    public static final CogwheelChainType CHAIN = register("chain", new CogwheelChainType.Builder()
                    .breakEffectsBlock(() -> Blocks.IRON_CHAIN)
                    .build());

    public static final CogwheelChainType BELT = register("belt", new CogwheelChainType.Builder()
                    .relatedItem(() -> AllItems.BELT_CONNECTOR)
                    .renderType(CogwheelChainType.ChainRenderInfo.BELT)
                    .renderTexture(CreateBitsnBobs.asResource("textures/block/chain_belt.png"))
                    .breakEffectsBlock(() -> Blocks.IRON_CHAIN)
                    .setCogwheelPredicate(BnbTags.BnbBlockTags.FLANGED_COGWHEEL::matches)
                    .permitsAxisChange(false)
                    .build());

    public static final CogwheelChainType ROPE_CHAIN = register("rope", new CogwheelChainType.Builder()
                    .relatedTag(CommonTags.ROPES)
                    .renderType(CogwheelChainType.ChainRenderInfo.ROPE)
                    .renderTexture(CreateBitsnBobs.asResource("textures/block/chain_rope.png"))
                    .breakEffectsBlock(() -> Blocks.IRON_CHAIN)
                    .setCogwheelPredicate(BnbTags.BnbBlockTags.FLANGED_COGWHEEL::matches)
                    .build());


    private static CogwheelChainType register(final String name, final CogwheelChainType type) {
        return Registry.register(BnbRegistries.COGWHEEL_CHAIN_TYPES, CreateBitsnBobs.asResource(name), type);
    }

    public static void register() {
        if (BnbRegistries.COGWHEEL_CHAIN_TYPES.size() < 3)
            throw new IllegalStateException("Cogwheel chain types did not register");
    }

}

