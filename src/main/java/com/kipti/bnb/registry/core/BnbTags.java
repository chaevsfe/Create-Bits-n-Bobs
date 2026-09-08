package com.kipti.bnb.registry.core;

import net.minecraft.core.registries.Registries;
import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.foundation.data.Lang;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BnbTags {

    public enum BnbItemTags {

        CHAIRS,
        SUPPRESSIBLE_COGWHEELS,
        SUPPRESSIBLE_DYED_LOGISTICS_COMPONENTS;

        public final TagKey<Item> tag;

        BnbItemTags() {
            final Identifier id = Identifier.fromNamespaceAndPath(CreateBitsnBobs.MOD_ID, Lang.asId(this.name()));
            this.tag = TagKey.create(Registries.ITEM, id);
        }

        @SuppressWarnings("deprecation")
        public boolean matches(final Item item) {
            return item.builtInRegistryHolder()
                    .is(this.tag);
        }

        public boolean matches(final ItemStack stack) {
            return stack.is(this.tag);
        }

        private static void register() {
        }

    }

    public enum BnbBlockTags {

        //TODO: if sable installed, use that
        LIGHT,
        HEAVY,
        SUPER_HEAVY,

        COGWHEEL_CHAIN_NO_SMALL_OFFSET,

        NIXIE_BOARDS,
        NIXIE_TUBES,

        CHAIRS,

        EXTRA_COGWHEEL_CHAIN_CANDIDATES,
        DEDICATED_COGWHEEL_CHAIN_COMPONENT,
        FORBIDDEN_COGWHEEL_CHAIN_COMPONENT,

        FLANGED_COGWHEEL,
        LARGE_FLANGED_COGWHEEL,
        SMALL_FLANGED_COGWHEEL,

        ENCASED_FLANGED_COGWHEEL,
        ENCASED_LARGE_FLANGED_COGWHEEL,

        COGWHEEL_MATERIAL_CANDIDATES,
        COGWHEEL_MATERIAL_COGWHEEL_MODEL,
        COGWHEEL_MATERIAL_SHAFTLESS_COGWHEEL_MODEL,
        COGWHEEL_MATERIAL_SHAFTLESS_LARGE_COGWHEEL_MODEL,
        COGWHEEL_MATERIAL_FLANGED_COGWHEEL_MODEL,
        COGWHEEL_MATERIAL_LARGE_FLANGED_COGWHEEL_MODEL,
        COGWHEEL_MATERIAL_ENCASED_FLANGED_COGWHEEL_MODEL,
        COGWHEEL_MATERIAL_ENCASED_LARGE_FLANGED_COGWHEEL_MODEL,

        //For checking blocks that may be dyeable in both BnB OR BnD
        DYEABLE_FLUID_TANK,
        DYEABLE_ITEM_VAULT;

        public final TagKey<Block> tag;

        BnbBlockTags() {
            final Identifier id = Identifier.fromNamespaceAndPath(CreateBitsnBobs.MOD_ID, Lang.asId(this.name()));
            this.tag = TagKey.create(Registries.BLOCK, id);
        }

        @SuppressWarnings("deprecation")
        public boolean matches(final Block item) {
            return item.builtInRegistryHolder()
                    .is(this.tag);
        }

        public boolean matches(final BlockState stack) {
            return stack.is(this.tag);
        }

        private static void register() {
        }

    }


    public static void register() {
        BnbItemTags.register();
        BnbBlockTags.register();
    }

}

