package com.kipti.bnb.foundation.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

/** The few convention tags this port needs, in Fabric's {@code c} namespace. */
public final class CommonTags {

    public static final TagKey<Item> ROPES = itemTag("ropes");
    public static final TagKey<EntityType<?>> TELEPORTING_NOT_SUPPORTED = entityTag("teleporting_not_supported");

    private CommonTags() {
    }

    private static TagKey<Item> itemTag(final String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }

    private static TagKey<EntityType<?>> entityTag(final String path) {
        return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", path));
    }
}
