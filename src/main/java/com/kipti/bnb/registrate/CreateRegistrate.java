package com.kipti.bnb.registrate;

import com.kipti.bnb.registrate.builders.BlockBuilder;
import com.kipti.bnb.registrate.builders.BlockEntityBuilder;
import com.kipti.bnb.registrate.builders.EntityBuilder;
import com.kipti.bnb.registrate.builders.ItemBuilder;
import com.kipti.bnb.registrate.fn.NonNullFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class CreateRegistrate extends Registrate {
    private static final java.util.Map<Item, ResourceKey<CreativeModeTab>> ITEM_TABS = new java.util.IdentityHashMap<>();

    private ResourceKey<CreativeModeTab> creativeTab;

    protected CreateRegistrate(String modid) {
        super(modid);
    }

    public static CreateRegistrate create(String modid) {
        return new CreateRegistrate(modid);
    }

    public <T extends Block> BlockBuilder<T, CreateRegistrate> block(String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return new BlockBuilder<>(this, this, name, factory);
    }

    public <T extends Item> ItemBuilder<T, CreateRegistrate> item(String name, NonNullFunction<Item.Properties, T> factory) {
        return new ItemBuilder<>(this, this, name, factory);
    }

    public <T extends BlockEntity> BlockEntityBuilder<T, CreateRegistrate> blockEntity(String name, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        return new BlockEntityBuilder<>(this, this, name, factory);
    }

    public <T extends Entity> EntityBuilder<T, CreateRegistrate> entity(String name, EntityType.EntityFactory<T> factory, MobCategory category) {
        return new EntityBuilder<>(this, this, name, factory, category);
    }

    public CreateRegistrate setCreativeTab(ResourceKey<CreativeModeTab> tab) {
        this.creativeTab = tab;
        return this;
    }

    @Override
    public void trackCreativeTab(Item item) {
        if (creativeTab != null)
            ITEM_TABS.put(item, creativeTab);
    }

    public static boolean isInCreativeTab(Item item, ResourceKey<CreativeModeTab> tab) {
        return ITEM_TABS.get(item) == tab;
    }
}
