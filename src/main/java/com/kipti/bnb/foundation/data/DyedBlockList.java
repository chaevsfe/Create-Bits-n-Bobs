package com.kipti.bnb.foundation.data;

import com.kipti.bnb.registrate.entry.BlockEntry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.Iterator;
import java.util.function.Function;

public class DyedBlockList<T extends Block> implements Iterable<BlockEntry<T>> {

    private final BlockEntry<T>[] entries;

    @SuppressWarnings("unchecked")
    public DyedBlockList(final Function<DyeColor, BlockEntry<T>> factory) {
        this.entries = new BlockEntry[DyeColor.values().length];
        for (final DyeColor color : DyeColor.values())
            this.entries[color.ordinal()] = factory.apply(color);
    }

    public BlockEntry<T> get(final DyeColor color) {
        return entries[color.ordinal()];
    }

    public boolean contains(final Block block) {
        for (final BlockEntry<T> entry : entries)
            if (entry != null && entry.is(block))
                return true;
        return false;
    }

    public BlockEntry<T>[] toArray() {
        return entries.clone();
    }

    @Override
    public Iterator<BlockEntry<T>> iterator() {
        return java.util.Arrays.stream(entries).iterator();
    }
}
