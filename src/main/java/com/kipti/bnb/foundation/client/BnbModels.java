package com.kipti.bnb.foundation.client;

import com.kipti.bnb.content.decoration.dyeable.BnbDyeableModels;
import com.kipti.bnb.content.decoration.grating.GratingPanelCTBehaviour;
import com.kipti.bnb.content.decoration.truss.TrussBlockModel;
import com.kipti.bnb.content.decoration.truss.TrussPipeBlockModel;
import com.kipti.bnb.content.decoration.weathered_girder.WeatheredConnectedGirderModel;
import com.kipti.bnb.content.trinkets.nixie.foundation.DoubleOrientedBlockModel;
import com.kipti.bnb.foundation.data.DyedBlockList;
import com.kipti.bnb.registry.content.blocks.BnbTrinketBlocks;
import com.kipti.bnb.foundation.EncasedBlockList;
import com.kipti.bnb.registrate.entry.BlockEntry;
import com.kipti.bnb.registry.client.BnbSpriteShifts;
import com.kipti.bnb.registry.content.blocks.deco.BnbDecorativeBlocks;
import com.kipti.bnb.registry.content.blocks.encased.BnbEncasedBlockLists;
import com.zurrtum.create.client.AllCTBehaviours;
import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.client.AllSpriteShifts;
import com.zurrtum.create.client.content.decoration.encasing.EncasedCTBehaviour;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import com.zurrtum.create.client.foundation.block.connected.SimpleCTBehaviour;
import com.zurrtum.create.client.infrastructure.model.CTModel;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Every block state model wrapper the port installs, run once Create Fly has registered its own. */
@Environment(EnvType.CLIENT)
public final class BnbModels {

    private BnbModels() {
    }

    public static void register() {
        BnbDyeableModels.register();

        AllModels.register(BnbDecorativeBlocks.WEATHERED_METAL_GIRDER.get(), WeatheredConnectedGirderModel.of());
        AllModels.register(BnbDecorativeBlocks.WEATHERED_METAL_GIRDER_ENCASED_SHAFT.get(), WeatheredConnectedGirderModel.of());

        AllModels.register(BnbDecorativeBlocks.INDUSTRIAL_TRUSS.get(), TrussBlockModel.of());
        AllModels.register(BnbDecorativeBlocks.METAL_TRUSS_SHAFT.get(), TrussBlockModel.of());
        AllModels.register(BnbDecorativeBlocks.METAL_TRUSS_PIPE.get(), TrussPipeBlockModel.of());

        connectedTextures(BnbDecorativeBlocks.INDUSTRIAL_GRATING.get(), new SimpleCTBehaviour(BnbSpriteShifts.INDUSTRIAL_GRATING));
        connectedTextures(BnbDecorativeBlocks.INDUSTRIAL_GRATING_PANEL.get(), new GratingPanelCTBehaviour(BnbSpriteShifts.INDUSTRIAL_GRATING));

        encasedTextures(BnbEncasedBlockLists.ENCASED_PISTON_EXTENSION_POLE, BnbModels::casingBehaviour);
        encasedTextures(BnbEncasedBlockLists.ENCASED_FLANGED_COGWHEEL, BnbModels::smallCogCasingBehaviour);
        encasedTextures(BnbEncasedBlockLists.ENCASED_LARGE_FLANGED_COGWHEEL, BnbModels::largeCogCasingBehaviour);

        AllModels.register(BnbTrinketBlocks.NIXIE_BOARD.get(), DoubleOrientedBlockModel.of());
        AllModels.register(BnbTrinketBlocks.LARGE_NIXIE_TUBE.get(), DoubleOrientedBlockModel.of());
        doubleOriented(BnbTrinketBlocks.DYED_NIXIE_BOARD);
        doubleOriented(BnbTrinketBlocks.DYED_LARGE_NIXIE_TUBE);
    }

    private static void doubleOriented(DyedBlockList<?> list) {
        for (BlockEntry<?> entry : list)
            AllModels.register(entry.get(), DoubleOrientedBlockModel.of());
    }

    private static void connectedTextures(Block block, ConnectedTextureBehaviour behaviour) {
        AllModels.register(block, CTModel.of(behaviour));
    }

    /**
     * Only the andesite and brass casings have a connected texture upstream; the two iron casings are plain, so
     * their encased variants get no wrapper at all.
     */
    private static void encasedTextures(EncasedBlockList<?> list,
                                        Function<EncasedBlockList.CasingMaterial, ConnectedTextureBehaviour> behaviour) {
        for (EncasedBlockList.CasingMaterial casing : EncasedBlockList.CasingMaterial.values()) {
            ConnectedTextureBehaviour ct = behaviour.apply(casing);
            if (ct == null)
                continue;
            BlockEntry<?> entry = list.get(casing);
            if (entry == null)
                continue;
            connectedTextures(entry.get(), ct);
        }
    }

    private static ConnectedTextureBehaviour casingBehaviour(EncasedBlockList.CasingMaterial casing) {
        CTSpriteShiftEntry shift = casingShift(casing);
        return shift == null ? null : new EncasedCTBehaviour(shift);
    }

    private static ConnectedTextureBehaviour smallCogCasingBehaviour(EncasedBlockList.CasingMaterial casing) {
        return switch (casing) {
            case ANDESITE -> AllCTBehaviours.COG_SIDE_ANDESITE_CASING;
            case BRASS -> AllCTBehaviours.COG_SIDE_BRASS_CASING;
            default -> null;
        };
    }

    private static ConnectedTextureBehaviour largeCogCasingBehaviour(EncasedBlockList.CasingMaterial casing) {
        return switch (casing) {
            case ANDESITE -> AllCTBehaviours.COG_ANDESITE_CASING;
            case BRASS -> AllCTBehaviours.COG_BRASS_CASING;
            default -> null;
        };
    }

    private static CTSpriteShiftEntry casingShift(EncasedBlockList.CasingMaterial casing) {
        return switch (casing) {
            case ANDESITE -> AllSpriteShifts.ANDESITE_CASING;
            case BRASS -> AllSpriteShifts.BRASS_CASING;
            default -> null;
        };
    }
}
