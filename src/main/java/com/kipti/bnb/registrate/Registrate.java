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
package com.kipti.bnb.registrate;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import com.kipti.bnb.registrate.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Registrate {
    private static final List<Consumer<ClientHookSink>> CLIENT_HOOKS = new ArrayList<>();

    private final String modid;
    private final Map<ResourceKey<? extends Registry<?>>, List<RegistryEntry<?, ?>>> tracked = new LinkedHashMap<>();
    private final List<Runnable> deferred = new ArrayList<>();
    private boolean flushed;

    protected Registrate(String modid) {
        this.modid = modid;
    }

    public String getModid() {
        return modid;
    }

    public Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(modid, path);
    }

    public void track(ResourceKey<? extends Registry<?>> registry, RegistryEntry<?, ?> entry) {
        tracked.computeIfAbsent(registry, key -> new ArrayList<>()).add(entry);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<RegistryEntry<T, ? extends T>> getAll(ResourceKey<? extends Registry<T>> registry) {
        List<RegistryEntry<?, ?>> entries = tracked.get(registry);
        if (entries == null)
            return List.of();
        return (Collection<RegistryEntry<T, ? extends T>>) (Collection<?>) Collections.unmodifiableList(entries);
    }

    public void queueAfterRegister(Runnable callback) {
        if (flushed) {
            callback.run();
            return;
        }
        deferred.add(callback);
    }

    public void registerTooltipModifier(Item item) {
    }

    public void trackCreativeTab(Item item) {
    }

    public void register() {
        flushed = true;
        List<Runnable> pending = new ArrayList<>(deferred);
        deferred.clear();
        for (Runnable callback : pending)
            callback.run();
    }

    public static void addClientHook(Consumer<ClientHookSink> hook) {
        CLIENT_HOOKS.add(hook);
    }

    public static List<Consumer<ClientHookSink>> drainClientHooks() {
        List<Consumer<ClientHookSink>> hooks = new ArrayList<>(CLIENT_HOOKS);
        CLIENT_HOOKS.clear();
        return hooks;
    }
}
