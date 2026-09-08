package com.kipti.bnb.registry.datagen;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.registrate.CreateRegistrate;
import com.kipti.bnb.registrate.entry.RegistryEntry;
import com.kipti.bnb.registry.content.BnbItems;
import com.kipti.bnb.registry.content.blocks.BnbKineticBlocks;
import com.kipti.bnb.registry.content.blocks.BnbTrinketBlocks;
import com.kipti.bnb.registry.core.BnbFeatureFlag;
import com.kipti.bnb.registry.worldgen.BnbPaletteStoneTypes;
import com.zurrtum.create.content.decoration.encasing.EncasedBlock;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class BnbCreativeTabs {

    public static final ResourceKey<CreativeModeTab> BASE_CREATIVE_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateBitsnBobs.asResource("bnb_based"));

    public static final ResourceKey<CreativeModeTab> PALETTES_CREATIVE_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateBitsnBobs.asResource("bnb_palettes"));

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, BASE_CREATIVE_TAB, FabricCreativeModeTab.builder()
                .title(Component.translatable("tab." + CreateBitsnBobs.MOD_ID + ".base"))
                .icon(BnbKineticBlocks.SMALL_FLANGED_COGWHEEL::asStack)
                .displayItems((parameters, output) -> buildCreativeTabContents(output, BASE_CREATIVE_TAB))
                .build());

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, PALETTES_CREATIVE_TAB, FabricCreativeModeTab.builder()
                .title(Component.translatable("tab." + CreateBitsnBobs.MOD_ID + ".deco"))
                .icon(() -> BnbPaletteStoneTypes.ASURINE.getVariants().registeredBlocks.getFirst().asStack())
                .displayItems((parameters, output) -> buildCreativeTabContents(output, PALETTES_CREATIVE_TAB))
                .build());
    }

    private static boolean matchesBlockFilter(final BlockItem item) {
        if (BnbTrinketBlocks.CHAIRS.contains(item.getBlock()) && !BnbTrinketBlocks.CHAIRS.get(DyeColor.RED).is(item.getBlock()))
            return false;

        return !(item.getBlock() instanceof EncasedBlock);
    }

    private static boolean matchesSearchOnlyBlockFilter(final BlockItem item) {
        return BnbTrinketBlocks.CHAIRS.contains(item.getBlock()) && !BnbTrinketBlocks.CHAIRS.get(DyeColor.RED).is(item.getBlock());
    }

    private static void buildCreativeTabContents(final CreativeModeTab.Output output, final ResourceKey<CreativeModeTab> tab) {
        for (final RegistryEntry<Item, ? extends Item> item : CreateBitsnBobs.REGISTRATE.getAll(Registries.ITEM)) {
            if (!CreateRegistrate.isInCreativeTab(item.get(), tab)
                    || !(item.get() instanceof final BlockItem blockItem)
                    || !BnbFeatureFlag.isEnabled(blockItem))
                continue;

            if (matchesSearchOnlyBlockFilter(blockItem))
                output.accept(item.get(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
            else if (matchesBlockFilter(blockItem))
                output.accept(item.get());
        }

        for (final RegistryEntry<Item, ? extends Item> item : CreateBitsnBobs.REGISTRATE.getAll(Registries.ITEM)) {
            if (!CreateRegistrate.isInCreativeTab(item.get(), tab) || item.get() instanceof BlockItem)
                continue;

            if (matchesItemFilter(item.get()) && BnbFeatureFlag.isEnabled(item.get()))
                output.accept(item.get());
        }
    }

    private static boolean matchesItemFilter(final Item item) {
        return item != BnbItems.TEST_ROPE.get();
    }

}
