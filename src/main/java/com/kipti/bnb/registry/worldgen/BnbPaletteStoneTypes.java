package com.kipti.bnb.registry.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import com.zurrtum.create.AllBlocks;
import com.kipti.bnb.content.decoration.palette.BnbPaletteBlockPattern;
import com.kipti.bnb.content.decoration.palette.BnbPalettesVariantEntry;
import com.kipti.bnb.registry.datagen.BnbCreativeTabs;
import com.zurrtum.create.Create;
import com.kipti.bnb.registrate.CreateRegistrate;
import com.kipti.bnb.registrate.builders.BlockBuilder;
import com.kipti.bnb.registrate.fn.NonNullFunction;
import com.kipti.bnb.registrate.fn.NonNullSupplier;
import com.kipti.bnb.registrate.fn.NonNullUnaryOperator;
import com.kipti.bnb.foundation.data.Lang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

import static com.kipti.bnb.content.decoration.palette.BnbPaletteBlockPattern.ADDITIONS_TO_BASE;

public enum BnbPaletteStoneTypes {

    GRANITE(ADDITIONS_TO_BASE, r -> () -> Blocks.GRANITE),
    DIORITE(ADDITIONS_TO_BASE, r -> () -> Blocks.DIORITE),
    ANDESITE(ADDITIONS_TO_BASE, r -> () -> Blocks.ANDESITE),
    CALCITE(ADDITIONS_TO_BASE, r -> () -> Blocks.CALCITE),
    DRIPSTONE(ADDITIONS_TO_BASE, r -> () -> Blocks.DRIPSTONE_BLOCK),
    DEEPSLATE(ADDITIONS_TO_BASE, r -> () -> Blocks.DEEPSLATE),
    TUFF(ADDITIONS_TO_BASE, r -> () -> Blocks.TUFF),

    ASURINE(ADDITIONS_TO_BASE, r -> () -> AllBlocks.ASURINE, (p) -> p.destroyTime(1.25f).mapColor(MapColor.COLOR_BLUE)),
    CRIMSITE(ADDITIONS_TO_BASE, r -> () -> AllBlocks.CRIMSITE, (p) -> p.destroyTime(1.25f).mapColor(MapColor.COLOR_RED)),
    LIMESTONE(ADDITIONS_TO_BASE, r -> () -> AllBlocks.LIMESTONE, (p) -> p.destroyTime(1.25f).mapColor(MapColor.SAND)),
    OCHRUM(ADDITIONS_TO_BASE, r -> () -> AllBlocks.OCHRUM, (p) -> p.destroyTime(1.25f).mapColor(MapColor.TERRACOTTA_YELLOW)),
    SCORIA(ADDITIONS_TO_BASE, r -> () -> AllBlocks.SCORIA, (p) -> p.destroyTime(1.25f).mapColor(MapColor.COLOR_BROWN)),
    SCORCHIA(ADDITIONS_TO_BASE, r -> () -> AllBlocks.SCORCHIA, (p) -> p.destroyTime(1.25f).mapColor(MapColor.TERRACOTTA_GRAY)),
    VERIDIUM(ADDITIONS_TO_BASE, r -> () -> AllBlocks.VERIDIUM, (p) -> p.destroyTime(1.25f).mapColor(MapColor.WARPED_NYLIUM)),

    ;

    private final Function<CreateRegistrate, NonNullSupplier<Block>> factory;
    private BnbPalettesVariantEntry variants;

    private NonNullSupplier<Block> baseBlock;
    public final NonNullFunction<BlockBuilder<? extends Block, CreateRegistrate>, BlockBuilder<? extends Block, CreateRegistrate>> modifyProperties;
    public final BnbPaletteBlockPattern[] variantTypes;
    public TagKey<Item> materialTag;

    BnbPaletteStoneTypes(final BnbPaletteBlockPattern[] variantTypes,
                         final Function<CreateRegistrate, NonNullSupplier<Block>> baseBlockSupplier) {
        this.factory = baseBlockSupplier;
        this.variantTypes = variantTypes;
        modifyProperties = b -> b.initialProperties(baseBlock);
    }

    BnbPaletteStoneTypes(final BnbPaletteBlockPattern[] variantTypes,
                         final Function<CreateRegistrate, NonNullSupplier<Block>> baseBlockSupplier,
                         final NonNullUnaryOperator<BlockBehaviour.Properties> modifyProperties) {
        this.factory = baseBlockSupplier;
        this.variantTypes = variantTypes;
        this.modifyProperties = b -> b.properties(modifyProperties);
    }

    public NonNullSupplier<Block> getBaseBlock() {
        return baseBlock;
    }

    public BnbPalettesVariantEntry getVariants() {
        return variants;
    }

    public static void register(final CreateRegistrate registrate) {
        registrate.setCreativeTab(BnbCreativeTabs.PALETTES_CREATIVE_TAB);
        for (final BnbPaletteStoneTypes paletteStoneVariants : values()) {
            paletteStoneVariants.baseBlock = paletteStoneVariants.factory.apply(registrate);
            final String id = Lang.asId(paletteStoneVariants.name());
            paletteStoneVariants.materialTag =
                    TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("create", "stone_types/" + id));
            paletteStoneVariants.variants = new BnbPalettesVariantEntry(id, paletteStoneVariants);
        }
        registrate.setCreativeTab(BnbCreativeTabs.BASE_CREATIVE_TAB);
    }

}

