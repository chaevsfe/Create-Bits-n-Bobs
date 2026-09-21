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
package com.kipti.bnb.registrate.client;

import com.zurrtum.create.client.AllBlockEntityRenders;
import com.zurrtum.create.client.AllEntityRenders;
import com.zurrtum.create.client.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import com.zurrtum.create.client.flywheel.lib.visualization.SimpleEntityVisualizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.kipti.bnb.registrate.ClientHookSink;
import com.kipti.bnb.registrate.Registrate;
import com.kipti.bnb.registrate.fn.NonNullFunction;
import com.kipti.bnb.utility.SimpleBlockEntityVisualFactory;
import com.kipti.bnb.utility.SimpleEntityVisualFactory;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class RegistrateClient implements ClientHookSink {
    private RegistrateClient() {
    }

    public static void flush(Registrate registrate) {
        RegistrateClient sink = new RegistrateClient();
        for (Consumer<ClientHookSink> hook : Registrate.drainClientHooks())
            hook.accept(sink);
    }

    @Override
    public <T extends BlockEntity> void renderer(BlockEntityType<T> type, Supplier<?> renderer) {
        AllBlockEntityRenders.render(type, provider(renderer));
    }

    @Override
    public <T extends BlockEntity> void visual(BlockEntityType<T> type, Supplier<?> renderer, Supplier<?> visual, Predicate<T> renderNormally) {
        SimpleBlockEntityVisualizer.Factory<T> factory = visualizer(visual);
        Predicate<T> skipVanillaRender = entity -> !renderNormally.test(entity);
        if (renderer == null) {
            new SimpleBlockEntityVisualizer.Builder<>(type).factory(factory).skipVanillaRender(skipVanillaRender).apply();
            return;
        }
        AllBlockEntityRenders.visual(type, provider(renderer), factory, skipVanillaRender);
    }

    @Override
    public <T extends Entity> void renderer(EntityType<T> type, Supplier<?> renderer) {
        AllEntityRenders.render(type, entityProvider(renderer));
    }

    @Override
    public <T extends Entity> void visual(EntityType<T> type, Supplier<?> renderer, Supplier<?> visual) {
        if (renderer != null)
            AllEntityRenders.render(type, entityProvider(renderer));
        new SimpleEntityVisualizer.Builder<>(type).factory(entityVisualizer(visual)).neverSkipVanillaRender().apply();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityRendererProvider<T> entityProvider(Supplier<?> renderer) {
        NonNullFunction<EntityRendererProvider.Context, EntityRenderer<T, ?>> function =
                (NonNullFunction<EntityRendererProvider.Context, EntityRenderer<T, ?>>) renderer.get();
        return function::apply;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> SimpleEntityVisualizer.Factory<T> entityVisualizer(Supplier<?> visual) {
        SimpleEntityVisualFactory<T> recorded = (SimpleEntityVisualFactory<T>) visual.get();
        return recorded::create;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityRendererProvider<T, ?> provider(Supplier<?> renderer) {
        NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<T, ?>> function =
                (NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<T, ?>>) renderer.get();
        return context -> castRenderer(function.apply(context));
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity, S extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState> BlockEntityRenderer<T, S> castRenderer(BlockEntityRenderer<T, ?> renderer) {
        return (BlockEntityRenderer<T, S>) renderer;
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> SimpleBlockEntityVisualizer.Factory<T> visualizer(Supplier<?> visual) {
        SimpleBlockEntityVisualFactory<T> recorded = (SimpleBlockEntityVisualFactory<T>) visual.get();
        return recorded::create;
    }
}
