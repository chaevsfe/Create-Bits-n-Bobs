package com.kipti.bnb.registry.content.blocks;

import com.kipti.bnb.content.kinetics.chain_pulley.ChainPulleyBlock;
import com.kipti.bnb.content.kinetics.cogwheel_carriage.block.CogwheelChainCarriageBlock;
import com.kipti.bnb.content.kinetics.cogwheel_carriage.block.CogwheelChainCarriageMovementBehaviour;
import com.kipti.bnb.content.kinetics.cogwheel_carriage.block.CogwheelChainCarriageMovingInteraction;
import com.kipti.bnb.content.kinetics.cogwheel_chain.block.EmptyFlangedGearBlock;
import com.kipti.bnb.content.kinetics.flywheel_bearing.FlywheelBearingBlock;
import com.kipti.bnb.content.kinetics.gigantic_cogwheel.GiganticCogwheelBlock;
import com.kipti.bnb.content.kinetics.gigantic_cogwheel.GiganticCogwheelSatelliteBlock;
import com.kipti.bnb.registry.core.BnbTags;
import com.zurrtum.create.AllBlockTags;
import com.zurrtum.create.api.stress.BlockStressValues;
import com.zurrtum.create.content.contraptions.pulley.PulleyBlock;
import com.kipti.bnb.foundation.data.SharedProperties;
import com.kipti.bnb.registrate.entry.BlockEntry;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.kipti.bnb.CreateBitsnBobs.REGISTRATE;
import static com.kipti.bnb.registrate.BnbBuilderHooks.interactionBehaviour;
import static com.kipti.bnb.registrate.BnbBuilderHooks.movementBehaviour;
import static com.kipti.bnb.foundation.data.TagGen.axeOrPickaxe;

public class BnbKineticBlocks {

    public static final BlockEntry<EmptyFlangedGearBlock> SMALL_FLANGED_COGWHEEL = REGISTRATE.block(
                    "small_flanged_cogwheel",
                    EmptyFlangedGearBlock::small
            )
            
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.sound(SoundType.WOOD)
                    .mapColor(MapColor.DIRT))
            .transform(axeOrPickaxe())
            
            .item()
            
            .build()
            
            .tag(BnbTags.BnbBlockTags.COGWHEEL_CHAIN_NO_SMALL_OFFSET.tag)
            
            .register();

    public static final BlockEntry<EmptyFlangedGearBlock> LARGE_FLANGED_COGWHEEL = REGISTRATE.block(
                    "large_flanged_cogwheel",
                    EmptyFlangedGearBlock::large
            )
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.sound(SoundType.WOOD)
                    .mapColor(MapColor.DIRT))
            .transform(axeOrPickaxe())
            
            .item()
            
            .build()
            
            .tag(BnbTags.BnbBlockTags.COGWHEEL_CHAIN_NO_SMALL_OFFSET.tag)
            
            .register();

    public static final BlockEntry<ChainPulleyBlock> CHAIN_PULLEY = REGISTRATE.block(
                    "chain_pulley",
                    ChainPulleyBlock::new
            )
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .properties(p -> p.noOcclusion())
            
            .transform(axeOrPickaxe())
            .tag(AllBlockTags.SAFE_NBT)
            
            .item()
            .build()
            .register();

    public static final BlockEntry<PulleyBlock.RopeBlock> CHAIN_ROPE = REGISTRATE.block(
                    "chain_rope",
                    PulleyBlock.RopeBlock::new
            )
            .properties(p -> p.sound(SoundType.CHAIN)
                    .mapColor(MapColor.COLOR_GRAY))
            .tag(AllBlockTags.BRITTLE)
            
            .tag(BlockTags.CLIMBABLE)
            
            .register();

    public static final BlockEntry<PulleyBlock.MagnetBlock> CHAIN_PULLEY_MAGNET =
            REGISTRATE.block("chain_pulley_magnet", PulleyBlock.MagnetBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .tag(AllBlockTags.BRITTLE)
                    .tag(BlockTags.CLIMBABLE)
                    
                    
                    .register();

    public static final BlockEntry<FlywheelBearingBlock> FLYWHEEL_BEARING =
            REGISTRATE.block("flywheel_bearing", FlywheelBearingBlock::new)
                    .transform(axeOrPickaxe())
                    .properties(p -> p.mapColor(MapColor.GOLD).noOcclusion())
                    
                    .tag(AllBlockTags.SAFE_NBT)
                    
                    .item()
                    
                    .build()
                    
                    .register();

    public static final BlockEntry<GiganticCogwheelBlock> GIGANTIC_COGWHEEL =
            REGISTRATE.block("gigantic_cogwheel", GiganticCogwheelBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p
                            .noOcclusion()
                            .isViewBlocking((state, level, pos) -> false))
                    .transform(axeOrPickaxe())
                    
                    .item()
                    
                    .build()
                    
                    .register();

    public static final BlockEntry<GiganticCogwheelSatelliteBlock> GIGANTIC_COGWHEEL_SATELLITE =
            REGISTRATE.block("gigantic_cogwheel_satellite", GiganticCogwheelSatelliteBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p
                            .noOcclusion()
                            .pushReaction(PushReaction.BLOCK)
                            .isViewBlocking((state, level, pos) -> false))
                    .transform(axeOrPickaxe())
                    
                    
                    .register();

    public static final BlockEntry<CogwheelChainCarriageBlock> COGWHEEL_CHAIN_CARRIAGE =
            REGISTRATE.block("cogwheel_chain_carriage", CogwheelChainCarriageBlock::new)
                    .transform(axeOrPickaxe())
                    .properties(p -> p.noOcclusion())
                    
                    
                    
                    .item()
                    
                    .build()
                    
                    .register();

    public static void register() {
    }

}