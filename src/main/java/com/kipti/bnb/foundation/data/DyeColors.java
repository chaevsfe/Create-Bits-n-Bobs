package com.kipti.bnb.foundation.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/** 26.2's DyeItem no longer exposes its colour, so the vanilla dye items are mapped by id instead. */
public final class DyeColors {

    private static final Map<Item, DyeColor> BY_ITEM = new IdentityHashMap<>();

    private DyeColors() {
    }

    @Nullable
    public static DyeColor of(final ItemStack stack) {
        return of(stack.getItem());
    }

    @Nullable
    public static DyeColor of(final Item item) {
        if (BY_ITEM.isEmpty())
            build();
        return BY_ITEM.get(item);
    }

    private static void build() {
        for (final DyeColor color : DyeColor.values()) {
            final Item item = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color.getSerializedName() + "_dye"));
            if (item != null)
                BY_ITEM.put(item, color);
        }
    }
}
