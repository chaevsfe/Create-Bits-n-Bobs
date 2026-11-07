package com.kipti.bnb.foundation.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class SavedBlockStates {

    private SavedBlockStates() {
    }

    public static BlockState read(final HolderGetter<Block> blocks, final CompoundTag saved) {
        return NbtUtils.readBlockState(blocks, saved.contains("Name") ? renamed(saved) : saved);
    }

    private static CompoundTag renamed(final CompoundTag saved) {
        final CompoundTag tag = new CompoundTag();
        tag.put("id", saved.get("Name"));
        final Tag properties = saved.get("Properties");
        if (properties != null)
            tag.put("properties", properties);
        return tag;
    }
}
