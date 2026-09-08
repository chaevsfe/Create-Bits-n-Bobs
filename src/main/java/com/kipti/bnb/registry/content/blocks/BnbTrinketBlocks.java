package com.kipti.bnb.registry.content.blocks;

import com.kipti.bnb.registrate.fn.NonNullUnaryOperator;
import com.kipti.bnb.registry.content.BnbDisplayTargets;
import com.kipti.bnb.registry.content.BnbShapes;
import com.kipti.bnb.content.kinetics.throttle_lever.ThrottleLeverBlock;
import com.kipti.bnb.content.trinkets.chair.ChairBlock;
import com.kipti.bnb.content.trinkets.light.foundation.LightBlock;
import com.kipti.bnb.content.trinkets.light.headlamp.HeadlampBlock;
import com.kipti.bnb.content.trinkets.light.headlamp.HeadlampBlockItem;
import com.kipti.bnb.content.trinkets.light.lightbulb.LightbulbBlock;
import com.kipti.bnb.content.trinkets.nixie.large_nixie_tube.LargeNixieTubeBlockNixie;
import com.kipti.bnb.content.trinkets.nixie.nixie_board.NixieBoardBlockNixie;
import com.kipti.bnb.registry.core.BnbTags;
import com.zurrtum.create.AllDisplaySources;
import com.zurrtum.create.AllBlockTags;
import com.zurrtum.create.content.contraptions.actors.seat.SeatInteractionBehaviour;
import com.zurrtum.create.content.contraptions.actors.seat.SeatMovementBehaviour;
import com.kipti.bnb.foundation.data.DyedBlockList;
import com.kipti.bnb.foundation.data.SharedProperties;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.kipti.bnb.registrate.builders.BlockBuilder;
import com.kipti.bnb.registrate.entry.BlockEntry;
import com.kipti.bnb.registrate.fn.NonNullFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.kipti.bnb.CreateBitsnBobs.REGISTRATE;
import static com.kipti.bnb.registrate.BnbBuilderHooks.displaySource;
import static com.kipti.bnb.registrate.BnbBuilderHooks.displayTarget;
import static com.kipti.bnb.registrate.BnbBuilderHooks.interactionBehaviour;
import static com.kipti.bnb.registrate.BnbBuilderHooks.movementBehaviour;
import static com.kipti.bnb.foundation.data.TagGen.axeOnly;
import static com.kipti.bnb.foundation.data.TagGen.axeOrPickaxe;
import static com.kipti.bnb.foundation.data.TagGen.pickaxeOnly;

public class BnbTrinketBlocks {
    public static final BlockEntry<NixieBoardBlockNixie> NIXIE_BOARD = REGISTRATE.block(
                    "nixie_board",
                    p -> new NixieBoardBlockNixie(
                            p,
                            null
                    )
            )
            .tag(BnbTags.BnbBlockTags.NIXIE_BOARDS.tag)
            .transform(nixieBoard())
            .item()
            
            .build()
            
            .register();

    public static final DyedBlockList<NixieBoardBlockNixie> DYED_NIXIE_BOARD = new DyedBlockList<>(colour -> {
        String colourName = colour.getSerializedName();
        return REGISTRATE.block(colourName + "_nixie_board", p -> new NixieBoardBlockNixie(p, colour))
                .transform(nixieBoard())
                
                .tag(BnbTags.BnbBlockTags.NIXIE_BOARDS.tag)
                .register();
    });

    public static final BlockEntry<LargeNixieTubeBlockNixie> LARGE_NIXIE_TUBE = REGISTRATE.block(
                    "large_nixie_tube",
                    p -> new LargeNixieTubeBlockNixie(
                            p,
                            null
                    )
            )
            .tag(BnbTags.BnbBlockTags.NIXIE_TUBES.tag)
            .transform(largeNixieTube())
            .item()
            
            .build()
            
            .register();
    public static final DyedBlockList<LargeNixieTubeBlockNixie> DYED_LARGE_NIXIE_TUBE = new DyedBlockList<>(colour -> {
        String colourName = colour.getSerializedName();
        return REGISTRATE.block(colourName + "_large_nixie_tube", p -> new LargeNixieTubeBlockNixie(p, colour))
                .transform(largeNixieTube())
                
                .tag(BnbTags.BnbBlockTags.NIXIE_TUBES.tag)
                .register();
    });
    public static final BlockEntry<LightbulbBlock> LIGHTBULB = REGISTRATE.block("lightbulb", LightbulbBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(pickaxeOnly())
            
            .properties(p -> p
                    .noOcclusion()
                    .lightLevel(LightBlock::getLightLevel)
                    .emissiveRendering(state -> state.getValue(LightBlock.POWER) > 0)
                    .forceSolidOn())
            
            .item()
            
            .build()
            
            .register();
    public static final BlockEntry<HeadlampBlock> HEADLAMP = REGISTRATE.block("headlamp", HeadlampBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(pickaxeOnly())
            
//            .onRegister(CreateRegistrate.blockModel(() -> HeadlampModelBuilder::new))
            .properties(p -> p
                    .noOcclusion()
                    .lightLevel(LightBlock::getLightLevel)
                    .emissiveRendering(LightBlock::isEmissive)
                    .mapColor(DyeColor.ORANGE)
                    .forceSolidOn())
            
            .tag(AllBlockTags.SAFE_NBT)
            .item(HeadlampBlockItem::new)
            
            .build()
            
            .register();
    public static final BlockEntry<LightBlock> BRASS_LAMP = REGISTRATE.block(
                    "brass_lamp",
                    (p) -> new LightBlock(
                            p,
                            BnbShapes.BRASS_LAMP_SHAPE,
                            true
                    )
            )
            .initialProperties(SharedProperties::softMetal)
            .transform(pickaxeOnly())
            
            .properties(p -> p
                    .noOcclusion()
                    .lightLevel(LightBlock::getLightLevel)
                    .emissiveRendering(state -> state.getValue(LightBlock.POWER) > 0)
                    .mapColor(DyeColor.ORANGE)
                    .forceSolidOn())
            
            .item()
            
            .build()
            
            .register();
    public static final BlockEntry<ThrottleLeverBlock> THROTTLE_LEVER =
            REGISTRATE.block("throttle_lever", ThrottleLeverBlock::new)
                    .initialProperties(() -> Blocks.LEVER)
                    .transform(axeOrPickaxe())
                    .tag(AllBlockTags.SAFE_NBT)
                    
                    .item()
                    
                    .build()
                    
                    .register();

    public static final DyedBlockList<ChairBlock> CHAIRS = new DyedBlockList<>(colour -> {
        String colourName = colour.getSerializedName();
        SeatMovementBehaviour movementBehaviour = new SeatMovementBehaviour();
        SeatInteractionBehaviour interactionBehaviour = new SeatInteractionBehaviour();
        return REGISTRATE.block(colourName + "_chair", p -> new ChairBlock(p, colour))
                .initialProperties(SharedProperties::wooden)
                .properties(p -> p.mapColor(colour))
                .properties(BlockBehaviour.Properties::noOcclusion)
                .transform(axeOnly())
                .onRegister(movementBehaviour(movementBehaviour))
                .onRegister(interactionBehaviour(interactionBehaviour))
                .transform(displaySource(AllDisplaySources.ENTITY_NAME))
//            .onRegister(CreateRegistrate.blockModel(() -> ChairModelBuilder::new))
                
                
                
                .onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey(v, "block.bits_n_bobs.chair"))
                .tag(BnbTags.BnbBlockTags.CHAIRS.tag)
                .item()
                
                .tag(BnbTags.BnbItemTags.CHAIRS.tag)
                .build()
                
                .register();
    });

    public static <T extends NixieBoardBlockNixie, P> NonNullUnaryOperator<BlockBuilder<T, P>> nixieBoard() {
        return b -> b
                .initialProperties(SharedProperties::softMetal)
                .transform(displayTarget(BnbDisplayTargets.GENERIC_NIXIE_TARGET))
                .transform(pickaxeOnly())
                
                
                .properties(p -> p
                        .noOcclusion()
                        .mapColor(DyeColor.ORANGE)
                        .forceSolidOn())
                ;
    }

    public static <T extends LargeNixieTubeBlockNixie, P> NonNullUnaryOperator<BlockBuilder<T, P>> largeNixieTube() {
        return b -> b
                .initialProperties(SharedProperties::softMetal)
                .transform(displayTarget(BnbDisplayTargets.GENERIC_NIXIE_TARGET))
                .transform(pickaxeOnly())
                
                
                .properties(p -> p
                        .noOcclusion()
                        .mapColor(DyeColor.ORANGE)
                        .forceSolidOn())
                ;
    }

    public static void register() {
    }

}
