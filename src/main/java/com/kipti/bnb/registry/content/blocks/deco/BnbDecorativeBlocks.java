package com.kipti.bnb.registry.content.blocks.deco;

import com.cake.struts.content.block.StrutBlockItem;
import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.content.decoration.grating.GratingBlock;
import com.kipti.bnb.content.decoration.grating.GratingPanelBlock;
import com.kipti.bnb.content.decoration.grating.GratingPanelBlockItem;
import com.kipti.bnb.content.decoration.strut.CableStrutBlock;
import com.kipti.bnb.content.decoration.strut.GirderStrutBlock;
import com.kipti.bnb.content.decoration.truss.TrussBlock;
import com.kipti.bnb.content.decoration.truss.TrussBlockItem;
import com.kipti.bnb.content.decoration.truss.TrussFluidPipeBlock;
import com.kipti.bnb.content.decoration.truss.TrussShaftBlock;
import com.kipti.bnb.content.decoration.weathered_girder.WeatheredGirderBlock;
import com.kipti.bnb.content.decoration.weathered_girder.WeatheredGirderEncasedShaftBlock;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllBlockTags;
import com.kipti.bnb.registrate.BnbBuilderHooks;
import com.zurrtum.create.client.foundation.block.connected.SimpleCTBehaviour;
import com.kipti.bnb.foundation.data.SharedProperties;
import com.kipti.bnb.foundation.data.TagGen;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.kipti.bnb.registrate.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import static com.kipti.bnb.CreateBitsnBobs.REGISTRATE;
//import static com.kipti.bnb.content.decoration.strut.GirderStrutBlock.FLUSH_ANCHOR;
import static com.kipti.bnb.foundation.data.TagGen.pickaxeOnly;

public class BnbDecorativeBlocks {

    public static final BlockEntry<WeatheredGirderBlock> WEATHERED_METAL_GIRDER = REGISTRATE.block(
                    "weathered_metal_girder",
                    WeatheredGirderBlock::new
            )
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            
            
            .item()
            .build()
            .register();

    public static final BlockEntry<WeatheredGirderEncasedShaftBlock> WEATHERED_METAL_GIRDER_ENCASED_SHAFT = REGISTRATE
            .block("weathered_metal_girder_encased_shaft", WeatheredGirderEncasedShaftBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            
            
            
            .register();

    public static final BlockEntry<GirderStrutBlock> WEATHERED_GIRDER_STRUT = REGISTRATE.block(
                    "weathered_girder_strut",
                    p -> new GirderStrutBlock(
                            p,
                            BnbStrutDefinitions.WEATHERED_MODEL
                    )
            )
            .initialProperties(SharedProperties::softMetal)
            .transform(pickaxeOnly())
            .properties(p -> p.noOcclusion())
            
            
            .onRegisterAfter(
                    Registries.ITEM,
                    v -> ItemDescription.useKey(v, "block.bits_n_bobs.girder_strut")
            )
            .tag(AllBlockTags.SAFE_NBT)
            .item(StrutBlockItem::new)
            
            .build()
            
            .register();

    public static final BlockEntry<GirderStrutBlock> GIRDER_STRUT = REGISTRATE.block(
                    "girder_strut",
                    p -> new GirderStrutBlock(
                            p,
                            BnbStrutDefinitions.NORMAL_MODEL
                    )
            )
            .initialProperties(SharedProperties::softMetal)
            .transform(pickaxeOnly())
            .properties(p -> p.noOcclusion())
            
            
            .tag(AllBlockTags.SAFE_NBT)
            .item(StrutBlockItem::new)
            
            .build()
            
            .register();

    public static final BlockEntry<CableStrutBlock> CABLE_GIRDER_STRUT = REGISTRATE.block(
                    "cable_girder_strut",
                    p -> new CableStrutBlock(
                            p,
                            BnbStrutDefinitions.CABLE_MODEL,
                            BnbStrutDefinitions.CABLE_INFO
                    )
            )
            .initialProperties(SharedProperties::softMetal)
            .transform(pickaxeOnly())
            .properties(p -> p.noOcclusion()
                    .sound(SoundType.CHAIN))
            
            
            .tag(AllBlockTags.SAFE_NBT)
            .item(StrutBlockItem::new)
            
            .build()
            
            .register();

    public static final BlockEntry<GratingBlock> INDUSTRIAL_GRATING = CreateBitsnBobs.REGISTRATE.block(
                    "industrial_grating",
                    GratingBlock::new
            )
            .properties(p -> p.mapColor(MapColor.METAL)
                    .strength(0.6f, 6.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false))
            .transform(TagGen.pickaxeOnly())
            
            
            
            .tag(AllBlockTags.FAN_TRANSPARENT)
            .simpleItem()
            .register();

    public static final BlockEntry<GratingPanelBlock> INDUSTRIAL_GRATING_PANEL = CreateBitsnBobs.REGISTRATE.block(
                    "industrial_grating_panel",
                    GratingPanelBlock::new
            )
            .properties(p -> p.mapColor(MapColor.METAL)
                    .noOcclusion()
                    .strength(0.6f, 6.0f)
                    .sound(SoundType.METAL)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false))
            .transform(TagGen.pickaxeOnly())
            
            
            
            .tag(AllBlockTags.FAN_TRANSPARENT)
            .item(GratingPanelBlockItem::new)
            
            .build()
            
            .register();

    public static final BlockEntry<TrussBlock> INDUSTRIAL_TRUSS = CreateBitsnBobs.REGISTRATE.block(
                    "industrial_truss",
                    TrussBlock::new
            )
            .initialProperties(SharedProperties::stone)
            .transform(pickaxeOnly())
            
            .properties(p -> p.mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
                    .noOcclusion())
            
            
            
            .item(TrussBlockItem::new)
            
            .build()
            
            .register();

    public static final BlockEntry<TrussFluidPipeBlock> METAL_TRUSS_PIPE =
            REGISTRATE.block("industrial_truss_pipe", TrussFluidPipeBlock::new)
                    
                    .initialProperties(SharedProperties::copperMetal)
                    .transform(pickaxeOnly())
                    .properties(p -> p.noOcclusion())
                    
                    
                    
                    
                    .register();

    public static final BlockEntry<TrussShaftBlock> METAL_TRUSS_SHAFT = REGISTRATE
            .block("industrial_truss_shaft", p -> new TrussShaftBlock(p, BnbDecorativeBlocks.INDUSTRIAL_TRUSS::get))
            .initialProperties(SharedProperties::stone)
            .transform(pickaxeOnly())
            
            .properties(BlockBehaviour.Properties::noOcclusion)
            
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .transform(BnbBuilderHooks.encasedVariantOf(() -> AllBlocks.SHAFT))
            
            .register();

//    public static final BlockEntry<TrussEncasedShaftBlock> INDUSTRIAL_TRUSS_ENCASED_SHAFT = CreateBitsnBobs.REGISTRATE
//            .block(
//                    "industrial_truss_encased_shaft",
//                    TrussEncasedShaftBlock::new
//            )
//            .properties(p -> p.mapColor(MapColor.METAL)
//                    .strength(0.1f, 6.0f)
//                    .sound(SoundType.METAL)
//                    .isSuffocating((state, level, pos) -> false)
//                    .isViewBlocking((state, level, pos) -> false)
//                    .noOcclusion())
//            .transform(TagGen.pickaxeOnly())
//            .blockstate(TrussBlockStateGen::trussBeamsBaseModel)
//            .onRegister(CreateRegistrate.blockModel(() -> TrussEncasedShaftModel::new))
//            .loot((p, b) -> p.add(
//                    b, p.createSingleItemTable(INDUSTRIAL_TRUSS.get())
//                            .withPool(p.applyExplosionCondition(
//                                    AllBlocks.SHAFT, LootPool.lootPool()
//                                            .setRolls(ConstantValue
//                                                              .exactly(1.0F))
//                                            .add(LootItem.lootTableItem(
//                                                    AllBlocks.SHAFT))
//                            ))
//            ))
//            .addLayer(() -> RenderType::cutout)
//            .register();

    public static void register() {
    }

}
