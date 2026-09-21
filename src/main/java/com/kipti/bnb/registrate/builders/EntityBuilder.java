/*
 * Copyright 2026 chaevsfe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kipti.bnb.registrate.builders;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.Nullable;
import com.kipti.bnb.registrate.Registrate;
import com.kipti.bnb.registrate.entry.EntityEntry;
import com.kipti.bnb.registrate.fn.NonNullConsumer;
import com.kipti.bnb.registrate.fn.NonNullFunction;
import com.kipti.bnb.registrate.fn.NonNullSupplier;
import com.kipti.bnb.utility.SimpleEntityVisualFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EntityBuilder<T extends Entity, P> extends AbstractBuilder<EntityType<T>, P, EntityBuilder<T, P>> {
    private final EntityType.EntityFactory<T> factory;
    private final MobCategory category;
    private final List<NonNullConsumer<EntityType.Builder<T>>> propertyOperators = new ArrayList<>();
    private Supplier<?> renderer;
    private Supplier<?> visualFactory;

    public EntityBuilder(Registrate owner, P parent, String name, EntityType.EntityFactory<T> factory, MobCategory category) {
        super(owner, name, parent);
        this.factory = factory;
        this.category = category;
    }

    public EntityBuilder<T, P> properties(NonNullConsumer<EntityType.Builder<T>> operator) {
        propertyOperators.add(operator);
        return this;
    }

    public EntityBuilder<T, P> renderer(NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T, ?>>> renderer) {
        this.renderer = renderer;
        return this;
    }

    public EntityBuilder<T, P> visual(NonNullSupplier<SimpleEntityVisualFactory<T>> visualFactory) {
        this.visualFactory = visualFactory;
        return this;
    }

    @Nullable
    protected Supplier<?> rendererSupplier() {
        return renderer;
    }

    protected void registerClientHooks(EntityType<T> type) {
        Supplier<?> factory = rendererSupplier();
        if (visualFactory != null) {
            Supplier<?> visual = visualFactory;
            Registrate.addClientHook(sink -> sink.visual(type, factory, visual));
            return;
        }
        if (factory == null)
            return;
        Registrate.addClientHook(sink -> sink.renderer(type, factory));
    }

    public EntityEntry<T> register() {
        Identifier id = getId();
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        EntityType.Builder<T> builder = EntityType.Builder.of(factory, category);
        for (NonNullConsumer<EntityType.Builder<T>> operator : propertyOperators)
            operator.accept(builder);
        EntityType<T> type = builder.build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
        EntityEntry<T> entry = new EntityEntry<>(id, type);
        getOwner().track(Registries.ENTITY_TYPE, entry);
        runRegisterCallbacks(type);
        queueAfterRegisterCallbacks(type);
        registerClientHooks(type);
        return entry;
    }
}
