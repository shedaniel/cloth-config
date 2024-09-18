/*
 * This file is part of Cloth Config.
 * Copyright (C) 2020 - 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.shedaniel.autoconfig.gui.registry;

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryHook;
import me.shedaniel.autoconfig.gui.registry.api.GuiTransformer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class GuiRegistry implements GuiRegistryAccess {
    
    private final Map<Priority, List<ProviderEntry>> providers = new EnumMap<>(Priority.class);
    private final List<TransformerEntry> transformers = new ArrayList<>();
    private final Map<HookEvent, List<HookEntry>> hooks = new EnumMap<>(HookEvent.class);
    
    public GuiRegistry() {}
    
    @Override
    public List<AbstractConfigListEntry> get(
            String i18n,
            Field field,
            Object config,
            Object defaults,
            GuiRegistryAccess registry
    ) {
        // EnumMap is ordered, so we can use providers.values() reliably.
        //
        // Reduce to the highest priority matching GuiProvider,
        // then use the provider to compute the return value.
        return providers.values().stream()
                .flatMap(List::stream)
                .filter(entry -> entry.predicate.test(field))
                .map(ProviderEntry::provider)
                .map(provider -> provider.get(i18n, field, config, defaults, registry))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<AbstractConfigListEntry> transform(
            List<AbstractConfigListEntry> guis,
            String i18n,
            Field field,
            Object config,
            Object defaults,
            GuiRegistryAccess registry
    ) {
        // Use each GuiTransformer to reduce the guis
        return this.transformers.stream()
                .filter(entry -> entry.predicate.test(field))
                .map(TransformerEntry::transformer)
                .reduce(guis,
                        (prevResult, transformer) -> transformer.transform(prevResult, i18n, field, config, defaults, registry),
                        (a, b) -> { throw new UnsupportedOperationException("Cannot transform GUIs in parallel!"); });
    }
    
    @Override
    public void runPreHook(String i18n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        Stream.ofNullable(hooks.get(HookEvent.PRE))
                .flatMap(List::stream)
                .filter(entry -> entry.predicate.test(field))
                .map(HookEntry::hook)
                .forEach(hook -> hook.run(Collections.emptyList(), i18n, field, config, defaults, registry));
    }
    
    @Override
    public void runPostHook(List<AbstractConfigListEntry> guis, String i18n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        var constGuis = Collections.unmodifiableList(guis);
        Stream.ofNullable(hooks.get(HookEvent.POST))
                .flatMap(List::stream)
                .filter(entry -> entry.predicate.test(field))
                .map(HookEntry::hook)
                .forEach(hook -> hook.run(constGuis, i18n, field, config, defaults, registry));
    }
    
    private void registerProvider(Priority priority, GuiProvider provider, Predicate<Field> predicate) {
        providers.computeIfAbsent(priority, p -> new ArrayList<>()).add(new ProviderEntry(predicate, provider));
    }
    
    public final void registerTypeProvider(GuiProvider provider, Class... types) {
        for (Class type : types) {
            registerProvider(Priority.LAST, provider, field -> type == field.getType());
        }
    }
    
    public final void registerPredicateProvider(GuiProvider provider, Predicate<Field> predicate) {
        registerProvider(Priority.NORMAL, provider, predicate);
    }
    
    @SafeVarargs
    public final void registerAnnotationProvider(GuiProvider provider, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            registerProvider(Priority.FIRST, provider, field -> field.isAnnotationPresent(type));
        }
    }
    
    @SafeVarargs
    public final void registerAnnotationProvider(GuiProvider provider, Predicate<Field> predicate, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            registerProvider(
                    Priority.FIRST,
                    provider,
                    field -> predicate.test(field) && field.isAnnotationPresent(type)
            );
        }
    }
    
    @SuppressWarnings("WeakerAccess")
    public void registerPredicateTransformer(GuiTransformer transformer, Predicate<Field> predicate) {
        transformers.add(new TransformerEntry(predicate, transformer));
    }
    
    @SafeVarargs
    public final void registerAnnotationTransformer(GuiTransformer transformer, Class<? extends Annotation>... types) {
        registerAnnotationTransformer(transformer, field -> true, types);
    }
    
    @SuppressWarnings("WeakerAccess")
    @SafeVarargs
    public final void registerAnnotationTransformer(GuiTransformer transformer, Predicate<Field> predicate, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            registerPredicateTransformer(transformer, field -> predicate.test(field) && field.isAnnotationPresent(type));
        }
    }
    
    private void registerHook(HookEvent event, GuiRegistryHook hook, Predicate<Field> predicate) {
        hooks.computeIfAbsent(event, e -> new ArrayList<>()).add(new HookEntry(predicate, hook));
    }
    
    @ApiStatus.Experimental
    public final void registerPreHook(GuiRegistryHook hook) {
        registerHook(HookEvent.PRE, hook, field -> true);
    }
    
    @ApiStatus.Experimental
    public final void registerPredicatePreHook(GuiRegistryHook hook, Predicate<Field> predicate) {
        registerHook(HookEvent.PRE, hook, predicate);
    }
    
    @ApiStatus.Experimental
    public final void registerPostHook(GuiRegistryHook hook) {
        registerHook(HookEvent.POST, hook, field -> true);
    }
    
    @ApiStatus.Experimental
    public final void registerPredicatePostHook(GuiRegistryHook hook, Predicate<Field> predicate) {
        registerHook(HookEvent.POST, hook, predicate);
    }
    
    private enum Priority {
        // Ordering is important: highest priority first
        FIRST,
        NORMAL,
        LAST
    }
    
    private enum HookEvent {
        PRE,
        POST
    }
    
    private record ProviderEntry(Predicate<Field> predicate, GuiProvider provider) {}
    private record TransformerEntry(Predicate<Field> predicate, GuiTransformer transformer) {}
    private record HookEntry(Predicate<Field> predicate, GuiRegistryHook hook) {}
}
