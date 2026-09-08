package com.kipti.bnb.content.decoration.palette;

import net.minecraft.core.registries.Registries;
import com.google.common.collect.ImmutableMap;
import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.registry.worldgen.BnbPaletteStoneTypes;
import com.kipti.bnb.registrate.CreateRegistrate;
import com.kipti.bnb.registrate.builders.BlockBuilder;
import com.kipti.bnb.registrate.builders.ItemBuilder;
import com.kipti.bnb.registrate.entry.BlockEntry;
import com.kipti.bnb.foundation.data.Lang;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;

import java.util.List;
import java.util.function.Supplier;

import static com.kipti.bnb.foundation.data.TagGen.pickaxeOnly;

public abstract class BnbPaletteBlockPartial<B extends Block> {

    public static final BnbPaletteBlockPartial<StairBlock> STAIR = new BnbPaletteBlockPartial.Stairs();
    public static final BnbPaletteBlockPartial<SlabBlock> SLAB = new BnbPaletteBlockPartial.Slab(false);
    public static final BnbPaletteBlockPartial<SlabBlock> UNIQUE_SLAB = new BnbPaletteBlockPartial.Slab(true);
    public static final BnbPaletteBlockPartial<WallBlock> WALL = new BnbPaletteBlockPartial.Wall();
    public static final BnbPaletteBlockPartial<StairBlock> TILE_STAIR = new BnbPaletteBlockPartial.TileStairs();
    public static final BnbPaletteBlockPartial<SlabBlock> TILE_SLAB = new BnbPaletteBlockPartial.TileSlab();
    public static final BnbPaletteBlockPartial<WallBlock> TILE_WALL = new BnbPaletteBlockPartial.TileWall();

    public static final BnbPaletteBlockPartial<?>[] ALL_PARTIALS = {STAIR, SLAB, WALL};
    public static final BnbPaletteBlockPartial<?>[] ALL_PARTIALS_TILE = {TILE_STAIR, TILE_SLAB, TILE_WALL};
    public static final BnbPaletteBlockPartial<?>[] FOR_POLISHED = {STAIR, UNIQUE_SLAB, WALL};

    private final String name;

    private BnbPaletteBlockPartial(final String name) {
        this.name = name;
    }

    public BlockBuilder<B, CreateRegistrate> create(final String variantName, final BnbPaletteBlockPattern pattern,
                                                                 final BlockEntry<? extends Block> block, final BnbPaletteStoneTypes variant) {
        final String patternName = Lang.nonPluralId(pattern.createName(variantName));
        final String blockName = patternName + "_" + this.name;

        final BlockBuilder<B, CreateRegistrate> blockBuilder = CreateBitsnBobs.REGISTRATE
                .block(blockName, p -> createBlock(block, p))
                .initialProperties(block::get)
                .transform(b -> transformBlock(b, variantName, pattern));

        final ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> itemBuilder = blockBuilder.item()
                .transform(b -> transformItem(b, variantName, pattern));

        if (canRecycle())
            itemBuilder.tag(variant.materialTag);

        return itemBuilder.build();
    }

    protected Identifier getTexture(final String variantName, final BnbPaletteBlockPattern pattern, final int index) {
        return BnbPaletteBlockPattern.toLocation(variantName, pattern.getTexture(index));
    }

    protected Identifier getFlippedTexture(final String variantName, final BnbPaletteBlockPattern pattern, final int index) {
        return BnbPaletteBlockPattern.toLocation(variantName, pattern.getTexture(index) + "_flipped");
    }

    protected BlockBuilder<B, CreateRegistrate> transformBlock(final BlockBuilder<B, CreateRegistrate> builder,
                                                               final String variantName, final BnbPaletteBlockPattern pattern) {
        getBlockTags().forEach(builder::tag);
        return builder.transform(pickaxeOnly());
    }

    protected ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> transformItem(
            final ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> builder, final String variantName,
            final BnbPaletteBlockPattern pattern) {
        getItemTags().forEach(builder::tag);
        return builder;
    }

    protected boolean canRecycle() {
        return true;
    }

    protected abstract Iterable<TagKey<Block>> getBlockTags();

    protected abstract Iterable<TagKey<Item>> getItemTags();

    protected abstract B createBlock(Supplier<? extends Block> block, BlockBehaviour.Properties properties);

    private static class Stairs extends BnbPaletteBlockPartial<StairBlock> {

        public Stairs() {
            super("stairs");
        }

        @Override
        protected StairBlock createBlock(final Supplier<? extends Block> block, final BlockBehaviour.Properties properties) {
            return new StairBlock(block.get().defaultBlockState(), properties);
        }


        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.STAIRS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("stairs")));
        }


    }

    private static class Slab extends BnbPaletteBlockPartial<SlabBlock> {

        private final boolean customSide;

        public Slab(final boolean customSide) {
            super("slab");
            this.customSide = customSide;
        }

        @Override
        protected SlabBlock createBlock(final Supplier<? extends Block> block, final BlockBehaviour.Properties properties) {
            return new SlabBlock(properties);
        }

        @Override
        protected boolean canRecycle() {
            return false;
        }


        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.SLABS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("slabs")));
        }


        @Override
        protected BlockBuilder<SlabBlock, CreateRegistrate> transformBlock(
                final BlockBuilder<SlabBlock, CreateRegistrate> builder, final String variantName, final BnbPaletteBlockPattern pattern) {
            return super.transformBlock(builder, variantName, pattern);
        }

    }

    //Are these the right ways to do tile stairs/slabs/walls? No.

    private static class TileStairs extends BnbPaletteBlockPartial<StairBlock> {

        public TileStairs() {
            super("stairs");
        }

        @Override
        protected StairBlock createBlock(final Supplier<? extends Block> block, final BlockBehaviour.Properties properties) {
            return new StairBlock(block.get().defaultBlockState(), properties);
        }

        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.STAIRS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("stairs")));
        }


    }

    private static class TileSlab extends BnbPaletteBlockPartial<SlabBlock> {

        public TileSlab() {
            super("slab");
        }

        @Override
        protected SlabBlock createBlock(final Supplier<? extends Block> block, final BlockBehaviour.Properties properties) {
            return new SlabBlock(properties);
        }

        @Override
        protected boolean canRecycle() {
            return false;
        }


        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.SLABS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("slabs")));
        }


        @Override
        protected BlockBuilder<SlabBlock, CreateRegistrate> transformBlock(
                final BlockBuilder<SlabBlock, CreateRegistrate> builder, final String variantName, final BnbPaletteBlockPattern pattern) {
            return super.transformBlock(builder, variantName, pattern);
        }

    }

    private static class Wall extends BnbPaletteBlockPartial<WallBlock> {

        public Wall() {
            super("wall");
        }

        @Override
        protected WallBlock createBlock(final Supplier<? extends Block> block, final BlockBehaviour.Properties properties) {
            return new WallBlock(properties.forceSolidOn());
        }

        @Override
        protected ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> transformItem(
                final ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> builder, final String variantName,
                final BnbPaletteBlockPattern pattern) {
            return super.transformItem(builder, variantName, pattern);
        }


        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.WALLS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(ItemTags.WALLS);
        }


    }

    private static class TileWall extends BnbPaletteBlockPartial<WallBlock> {

        public TileWall() {
            super("wall");
        }

        @Override
        protected WallBlock createBlock(final Supplier<? extends Block> block, final BlockBehaviour.Properties properties) {
            return new WallBlock(properties.forceSolidOn());
        }

        @Override
        protected ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> transformItem(
                final ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> builder, final String variantName,
                final BnbPaletteBlockPattern pattern) {
            final Identifier wallTexture = getTexture(variantName, pattern, 0);
            final Identifier wallTextureFlipped = getFlippedTexture(variantName, pattern, 0);


            return super.transformItem(builder, variantName, pattern);
        }

        public static final ImmutableMap<Direction, Property<WallSide>> WALL_PROPS = ImmutableMap.<Direction, Property<WallSide>>builder()
                .put(Direction.EAST, BlockStateProperties.EAST_WALL)
                .put(Direction.NORTH, BlockStateProperties.NORTH_WALL)
                .put(Direction.SOUTH, BlockStateProperties.SOUTH_WALL)
                .put(Direction.WEST, BlockStateProperties.WEST_WALL)
                .build();

        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.WALLS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(ItemTags.WALLS);
        }

    }
}


