package com.kipti.bnb.content.decoration.palette;

import com.google.common.collect.ImmutableList;
import com.kipti.bnb.registry.worldgen.BnbPaletteStoneTypes;
import com.kipti.bnb.registrate.CreateRegistrate;
import com.kipti.bnb.registrate.builders.BlockBuilder;
import com.kipti.bnb.registrate.builders.ItemBuilder;
import com.kipti.bnb.registrate.entry.BlockEntry;
import com.kipti.bnb.registrate.fn.NonNullSupplier;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.kipti.bnb.CreateBitsnBobs.REGISTRATE;
import static com.kipti.bnb.foundation.data.TagGen.pickaxeOnly;

public class BnbPalettesVariantEntry {

    public final ImmutableList<BlockEntry<? extends Block>> registeredBlocks;
    public final ImmutableList<BlockEntry<? extends Block>> registeredPartials;

    public BnbPalettesVariantEntry(final String name, final BnbPaletteStoneTypes paletteStoneVariants) {
        final ImmutableList.Builder<BlockEntry<? extends Block>> registeredBlocks = ImmutableList.builder();
        final ImmutableList.Builder<BlockEntry<? extends Block>> registeredPartials = ImmutableList.builder();
        final NonNullSupplier<Block> baseBlock = paletteStoneVariants.getBaseBlock();

        for (final BnbPaletteBlockPattern pattern : paletteStoneVariants.variantTypes) {
            final BlockBuilder<? extends Block, CreateRegistrate> builder =
                    paletteStoneVariants.modifyProperties.apply(REGISTRATE.block(pattern.createName(name), pattern.getBlockFactory()))
                            .transform(pickaxeOnly())
                            ;

            final ItemBuilder<BlockItem, ? extends BlockBuilder<? extends Block, CreateRegistrate>> itemBuilder =
                    builder.item();

            final TagKey<Block>[] blockTags = pattern.getBlockTags();
            if (blockTags != null)
                builder.tag(blockTags);
            final TagKey<Item>[] itemTags = pattern.getItemTags();
            if (itemTags != null)
                itemBuilder.tag(itemTags);

            itemBuilder.tag(paletteStoneVariants.materialTag);


            final BlockEntry<? extends Block> block = builder.register();
            registeredBlocks.add(block);

            for (final BnbPaletteBlockPartial<? extends Block> partialBlock : pattern.getPartials())
                registeredPartials.add(partialBlock.create(name, pattern, block, paletteStoneVariants)
                        .register());
        }


        this.registeredBlocks = registeredBlocks.build();
        this.registeredPartials = registeredPartials.build();
    }

}

