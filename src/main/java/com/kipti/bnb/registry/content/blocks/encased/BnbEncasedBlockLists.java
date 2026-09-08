package com.kipti.bnb.registry.content.blocks.encased;

import com.kipti.bnb.content.kinetics.encased_blocks.cogwheel.BnbEncasedFlangedCogBlock;
import com.kipti.bnb.content.kinetics.encased_blocks.piston_pole.EncasedPistonExtensionPoleBlock;
import com.kipti.bnb.foundation.BnbBuilderTransformers;
import com.kipti.bnb.foundation.EncasedBlockList;
import com.kipti.bnb.registry.content.blocks.BnbKineticBlocks;
import com.kipti.bnb.registry.core.BnbTags;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.decoration.encasing.EncasableBlock;
import com.kipti.bnb.registrate.BnbBuilderHooks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.kipti.bnb.CreateBitsnBobs.REGISTRATE;
import static com.kipti.bnb.foundation.data.TagGen.axeOrPickaxe;

public class BnbEncasedBlockLists {

    public static final EncasedBlockList<EncasedPistonExtensionPoleBlock> ENCASED_PISTON_EXTENSION_POLE = new EncasedBlockList<>(
            (casing) -> REGISTRATE
                    .block(
                            casing.asId("encased_piston_extension_pole"),
                            (p) -> new EncasedPistonExtensionPoleBlock(p, casing.getMaterial())
                    )
                    .initialProperties(() -> Blocks.PISTON_HEAD)
                    .properties(p -> p.sound(SoundType.SCAFFOLDING)
                            .mapColor(MapColor.DIRT)
                            .forceSolidOn())
                    .transform(BnbBuilderHooks.encasedVariantOf(() -> AllBlocks.PISTON_EXTENSION_POLE))
                    .transform(axeOrPickaxe())
                    
                    
                    
                    .simpleItem()
                    .register());

    public static final EncasedBlockList<BnbEncasedFlangedCogBlock> ENCASED_FLANGED_COGWHEEL = new EncasedBlockList<>(
            casing ->
                    REGISTRATE.block(
                                    casing.asId("encased_flanged_cogwheel"),
                                    p -> new BnbEncasedFlangedCogBlock(p, false, casing.getMaterial(),
                                            BnbKineticBlocks.SMALL_FLANGED_COGWHEEL::get)
                            )
                            .properties(p -> p.mapColor(MapColor.PODZOL))
                            .transform(BnbBuilderTransformers.casingMaterialCogwheelBase(
                                    casing,
                                    BnbKineticBlocks.SMALL_FLANGED_COGWHEEL::get,
                                    false
                            ))
                            .transform(BnbBuilderHooks.encasedVariantOf(() -> BnbKineticBlocks.SMALL_FLANGED_COGWHEEL.get()))
                            
                            .tag(BnbTags.BnbBlockTags.COGWHEEL_CHAIN_NO_SMALL_OFFSET.tag)
                            .tag(BnbTags.BnbBlockTags.ENCASED_FLANGED_COGWHEEL.tag)
                            .register());

    public static final EncasedBlockList<BnbEncasedFlangedCogBlock> ENCASED_LARGE_FLANGED_COGWHEEL = new EncasedBlockList<>(
            casing ->
                    REGISTRATE.block(
                                    casing.asId("encased_large_flanged_cogwheel"),
                                    p -> new BnbEncasedFlangedCogBlock(p, true, casing.getMaterial(),
                                            BnbKineticBlocks.LARGE_FLANGED_COGWHEEL::get)
                            )
                            .properties(p -> p.mapColor(MapColor.PODZOL))
                            .transform(BnbBuilderTransformers.casingMaterialCogwheelBase(
                                    casing,
                                    BnbKineticBlocks.LARGE_FLANGED_COGWHEEL::get,
                                    true
                            ))
                            .transform(BnbBuilderHooks.encasedVariantOf(() -> BnbKineticBlocks.LARGE_FLANGED_COGWHEEL.get()))
                            
                            .tag(BnbTags.BnbBlockTags.COGWHEEL_CHAIN_NO_SMALL_OFFSET.tag)
                            .tag(BnbTags.BnbBlockTags.ENCASED_LARGE_FLANGED_COGWHEEL.tag)
                            .register());

    public static void register() {
    }

}
