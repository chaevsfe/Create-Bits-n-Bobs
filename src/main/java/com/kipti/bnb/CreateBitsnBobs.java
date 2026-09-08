package com.kipti.bnb;

import com.kipti.bnb.content.trinkets.light.headlamp.HeadlampBlock;
import com.kipti.bnb.registry.content.BnbAdvancements;
import com.kipti.bnb.foundation.behaviour.BnbBehaviourEvents;
import com.kipti.bnb.foundation.config.conditions.BnbResourceConditions;
import com.kipti.bnb.content.kinetics.cogwheel_chain.types.BnbCogwheelChainTypes;
import com.kipti.bnb.network.BnbPackets;
import com.kipti.bnb.registrate.CreateRegistrate;
import com.kipti.bnb.registry.azimuth.BnbBehaviourApplicators;
import com.kipti.bnb.registry.compat.BnbCreateStresses;
import com.kipti.bnb.registry.content.BnbBlockEntities;
import com.kipti.bnb.registry.content.BnbBlocksBootstrap;
import com.kipti.bnb.registry.content.BnbContraptionTypes;
import com.kipti.bnb.registry.content.BnbEntityTypes;
import com.kipti.bnb.registry.content.BnbItems;
import com.kipti.bnb.registry.core.BnbConfigs;
import com.kipti.bnb.registry.core.BnbDataComponents;
import com.kipti.bnb.registry.core.BnbTags;
import com.kipti.bnb.registry.datagen.BnbCreativeTabs;
import com.kipti.bnb.registry.worldgen.BnbPaletteStoneTypes;
import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class CreateBitsnBobs {

    public static final String MOD_ID = "bits_n_bobs";
    public static final String NAME = "Create: Bits 'n' Bobs";
    public static final String TAB_NAME = "Bits 'n' Bobs";
    public static final String DECO_NAME = "Bits 'n' Bobs' Block Palettes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public static void registerBlocksEarly() {
        BnbBlocksBootstrap.register();
        BnbPaletteStoneTypes.register(REGISTRATE);
    }

    public static void init() {
        LOGGER.info("Bits 'n' Bobs is present!");

        warnAboutCogwheelAssetReplacement("dndecor", "Create: Design n' Decor");
        warnAboutCogwheelAssetReplacement("createcasing", "Create: Encased");

        BnbConfigs.register();
        BnbResourceConditions.register();

        BnbDataComponents.register();
        BnbCogwheelChainTypes.register();
        BnbItems.register();
        BnbEntityTypes.register();
        BnbBlockEntities.register();
        BnbContraptionTypes.register();
        BnbTags.register();
        BnbPackets.register();
        BnbCreativeTabs.register();
        BnbAdvancements.register();

        BnbCreateStresses.registerRedirects();
        BnbBehaviourApplicators.register();

        REGISTRATE.register();

        CommonEvents.register();
        BnbBehaviourEvents.register();
        HeadlampBlock.registerBreakHandler();
    }

    public static Identifier asResource(final String s) {
        return Identifier.fromNamespaceAndPath(MOD_ID, s);
    }

    private static void warnAboutCogwheelAssetReplacement(final String modId, final String modName) {
        if (FabricLoader.getInstance().isModLoaded(modId)) {
            LOGGER.warn("Bits 'n' bobs is replacing assets inside {} with ones using the default cogwheel model instead of the modified cogwheel model, this may cause some visual inconsitency!", modName);
        }
    }

}
