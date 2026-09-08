package com.kipti.bnb.foundation.config.conditions;

import com.kipti.bnb.CreateBitsnBobs;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.Identifier;

public class BnbResourceConditions {

    public static final Identifier FEATURE_ENABLED = CreateBitsnBobs.asResource("feature_enabled");
    public static final Identifier FEATURE_ITEM_ENABLED = CreateBitsnBobs.asResource("feature_item_enabled");

    public static void register() {
        ResourceConditions.register(BnbFeatureEnabledCondition.TYPE);
        ResourceConditions.register(BnbFeatureItemEnabledCondition.TYPE);

        if (ResourceConditions.getConditionType(FEATURE_ENABLED) != BnbFeatureEnabledCondition.TYPE)
            throw new IllegalStateException("Resource condition " + FEATURE_ENABLED + " did not register");
        if (ResourceConditions.getConditionType(FEATURE_ITEM_ENABLED) != BnbFeatureItemEnabledCondition.TYPE)
            throw new IllegalStateException("Resource condition " + FEATURE_ITEM_ENABLED + " did not register");
    }

}
