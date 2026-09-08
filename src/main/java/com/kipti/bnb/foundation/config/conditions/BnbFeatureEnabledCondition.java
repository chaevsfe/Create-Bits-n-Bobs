package com.kipti.bnb.foundation.config.conditions;

import com.kipti.bnb.registry.core.BnbFeatureFlag;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.NotNull;

public record BnbFeatureEnabledCondition(String featureFlagKey) implements ResourceCondition {

    public static final MapCodec<BnbFeatureEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(
            c -> c.group(
                    Codec.STRING.fieldOf("feature").forGetter(BnbFeatureEnabledCondition::featureFlagKey)
            ).apply(c, BnbFeatureEnabledCondition::new)
    );

    public static final ResourceConditionType<BnbFeatureEnabledCondition> TYPE =
            ResourceConditionType.create(BnbResourceConditions.FEATURE_ENABLED, CODEC);

    @Override
    public @NotNull ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(final RegistryOps.RegistryInfoLookup registryLookup) {
        return BnbFeatureFlag.isEnabled(this.featureFlagKey);
    }

}
