package com.kipti.bnb.foundation.config.conditions;

import com.kipti.bnb.registry.core.BnbFeatureFlag;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public record BnbFeatureItemEnabledCondition(ResourceKey<Item> itemKey) implements ResourceCondition {

    public static final MapCodec<BnbFeatureItemEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(
            c -> c.group(
                    ResourceKey.codec(BuiltInRegistries.ITEM.key()).fieldOf("item").forGetter(BnbFeatureItemEnabledCondition::itemKey)
            ).apply(c, BnbFeatureItemEnabledCondition::new)
    );

    public static final ResourceConditionType<BnbFeatureItemEnabledCondition> TYPE =
            ResourceConditionType.create(BnbResourceConditions.FEATURE_ITEM_ENABLED, CODEC);

    @Override
    public @NotNull ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(final RegistryOps.RegistryInfoLookup registryLookup) {
        return BuiltInRegistries.ITEM.getOptional(this.itemKey)
                .map(BnbFeatureFlag::isEnabled)
                .orElse(true);
    }

}
