package com.kipti.bnb.foundation.client.tooltip;

import com.kipti.bnb.CreateBitsnBobs;
import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.zurrtum.create.client.foundation.item.KineticStats;
import com.zurrtum.create.client.foundation.item.TooltipModifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

@Environment(EnvType.CLIENT)
public final class BnbTooltipModifiers {

    private BnbTooltipModifiers() {
    }

    public static void register() {
        CreateBitsnBobs.REGISTRATE.getAll(Registries.ITEM).forEach(entry -> register(entry.get()));
    }

    private static void register(final Item item) {
        TooltipModifier.REGISTRY.register(
                item,
                new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
        );
    }

}
