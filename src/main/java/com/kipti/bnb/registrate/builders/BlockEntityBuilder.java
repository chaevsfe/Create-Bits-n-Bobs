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

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.kipti.bnb.registrate.Registrate;
import com.kipti.bnb.registrate.entry.BlockEntityEntry;
import com.kipti.bnb.registrate.fn.NonNullFunction;
import com.kipti.bnb.registrate.fn.NonNullSupplier;
import com.kipti.bnb.utility.SimpleBlockEntityVisualFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlockEntityBuilder<T extends BlockEntity, P> extends AbstractBuilder<BlockEntityType<T>, P, BlockEntityBuilder<T, P>> {
    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    private final BlockEntityFactory<T> factory;
    private final List<NonNullSupplier<? extends Block>> validBlocks = new ArrayList<>();
    private Supplier<?> renderer;
    private Supplier<?> visualFactory;
    private Predicate<T> renderNormally = entity -> true;

    public BlockEntityBuilder(Registrate owner, P parent, String name, BlockEntityFactory<T> factory) {
        super(owner, name, parent);
        this.factory = factory;
    }

    public BlockEntityBuilder<T, P> validBlock(NonNullSupplier<? extends Block> block) {
        validBlocks.add(block);
        return this;
    }

    @SafeVarargs
    public final BlockEntityBuilder<T, P> validBlocks(NonNullSupplier<? extends Block>... blocks) {
        Collections.addAll(validBlocks, blocks);
        return this;
    }

    public BlockEntityBuilder<T, P> renderer(NonNullSupplier<NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T, ?>>> renderer) {
        this.renderer = renderer;
        return this;
    }

    public BlockEntityBuilder<T, P> visual(NonNullSupplier<SimpleBlockEntityVisualFactory<T>> visualFactory) {
        return visual(visualFactory, true);
    }

    public BlockEntityBuilder<T, P> visual(NonNullSupplier<SimpleBlockEntityVisualFactory<T>> visualFactory, boolean renderNormally) {
        return visual(visualFactory, entity -> renderNormally);
    }

    public BlockEntityBuilder<T, P> visual(NonNullSupplier<SimpleBlockEntityVisualFactory<T>> visualFactory, Predicate<T> renderNormally) {
        this.visualFactory = visualFactory;
        this.renderNormally = renderNormally;
        return this;
    }

    @Nullable
    protected Supplier<?> rendererSupplier() {
        return renderer;
    }

    protected BlockEntityType<T> createEntry() {
        Set<Block> blocks = new LinkedHashSet<>();
        for (NonNullSupplier<? extends Block> block : validBlocks)
            blocks.add(block.get());
        AtomicReference<BlockEntityType<T>> self = new AtomicReference<>();
        BlockEntityType<T> type = new BlockEntityType<>((pos, state) -> factory.create(self.get(), pos, state), blocks);
        self.set(type);
        return type;
    }

    protected void registerClientHooks(BlockEntityType<T> type) {
        Supplier<?> factory = rendererSupplier();
        if (visualFactory != null) {
            Supplier<?> visual = visualFactory;
            Predicate<T> normally = renderNormally;
            Registrate.addClientHook(sink -> sink.visual(type, factory, visual, normally));
            return;
        }
        if (factory == null)
            return;
        Registrate.addClientHook(sink -> sink.renderer(type, factory));
    }

    public BlockEntityEntry<T> register() {
        Identifier id = getId();
        BlockEntityType<T> type = createEntry();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type);
        BlockEntityEntry<T> entry = new BlockEntityEntry<>(id, type);
        getOwner().track(Registries.BLOCK_ENTITY_TYPE, entry);
        runRegisterCallbacks(type);
        queueAfterRegisterCallbacks(type);
        registerClientHooks(type);
        return entry;
    }
}
