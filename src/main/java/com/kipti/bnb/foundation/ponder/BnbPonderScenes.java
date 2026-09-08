package com.kipti.bnb.foundation.ponder;

import com.kipti.bnb.foundation.ponder.scenes.CogwheelChainScenes;
import com.kipti.bnb.foundation.ponder.scenes.DyedPipeScenes;
import com.kipti.bnb.foundation.ponder.scenes.DyedTankScenes;
import com.kipti.bnb.foundation.ponder.scenes.GirderStrutScenes;
import com.kipti.bnb.foundation.ponder.scenes.NixieDisplayScenes;
import com.kipti.bnb.registrate.entry.ItemProviderEntry;
import com.kipti.bnb.registrate.entry.RegistryEntry;
import com.kipti.bnb.registry.content.blocks.BnbKineticBlocks;
import com.kipti.bnb.registry.content.blocks.BnbTrinketBlocks;
import com.kipti.bnb.registry.content.blocks.deco.BnbDecorativeBlocks;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public class BnbPonderScenes {

    public static void register(final PonderSceneRegistrationHelper<Identifier> helper) {
        final PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        helper.forComponents(
                        BuiltInRegistries.ITEM.getKey(Items.IRON_CHAIN),
                        id(AllBlocks.COGWHEEL),
                        id(AllBlocks.LARGE_COGWHEEL),
                        BnbKineticBlocks.SMALL_FLANGED_COGWHEEL.getId(),
                        BnbKineticBlocks.LARGE_FLANGED_COGWHEEL.getId()
                )
                .addStoryBoard("chain_cog/flat", CogwheelChainScenes::flatCogwheelChain)
                .addStoryBoard("chain_cog/axis_change", CogwheelChainScenes::changingAxisCogwheelChain);

        helper.forComponents(
                        id(AllBlocks.FLUID_PIPE),
                        id(AllBlocks.ENCASED_FLUID_PIPE),
                        id(AllBlocks.GLASS_FLUID_PIPE)
                )
                .addStoryBoard("dyeable/dyed_pipes", DyedPipeScenes::dyedPipes);

        helper.forComponents(id(AllBlocks.FLUID_TANK))
                .addStoryBoard("dyeable/dyed_tank", DyedTankScenes::dyedTank);

        HELPER.forComponents(BnbTrinketBlocks.NIXIE_BOARD)
                .addStoryBoard("nixie/nixie_board", NixieDisplayScenes::nixieBoard);

        HELPER.forComponents(BnbTrinketBlocks.LARGE_NIXIE_TUBE)
                .addStoryBoard("nixie/large_nixie_tube", NixieDisplayScenes::largeNixieTube);

        HELPER.forComponents(BnbDecorativeBlocks.GIRDER_STRUT)
                .addStoryBoard("struts/industrial_strut",
                        (builder, util) -> GirderStrutScenes.girderStrut(builder, util, BnbDecorativeBlocks.GIRDER_STRUT));

        HELPER.forComponents(BnbDecorativeBlocks.WEATHERED_GIRDER_STRUT)
                .addStoryBoard("struts/weathered_strut",
                        (builder, util) -> GirderStrutScenes.girderStrut(builder, util, BnbDecorativeBlocks.WEATHERED_GIRDER_STRUT));

        HELPER.forComponents(BnbDecorativeBlocks.CABLE_GIRDER_STRUT)
                .addStoryBoard("struts/cable_strut",
                        (builder, util) -> GirderStrutScenes.girderStrut(builder, util, BnbDecorativeBlocks.CABLE_GIRDER_STRUT));
    }

    private static Identifier id(final Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}
