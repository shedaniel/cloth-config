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

import me.shedaniel.autoconfig.dependencies.DependencyManager;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.gui.registry.api.GuiTransformer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class GuiRegistry extends AbstractGuiRegistry {
    
    private Map<Priority, List<ProviderEntry>> providers = new HashMap<>();
    private List<TransformerEntry> transformers = new ArrayList<>();
    
    public GuiRegistry() {
        for (Priority priority : Priority.values()) {
            providers.put(priority, new ArrayList<>());
        }
    }
    
    private static <T> Optional<T> firstPresent(Stream<Supplier<Optional<T>>> optionals) {
        return optionals
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
    
    @Override
    public List<AbstractConfigListEntry> get(
            String i18n,
            Field field,
            Object config,
            Object defaults,
            GuiRegistryAccess registry
    ) {
        List<AbstractConfigListEntry> guis = firstPresent(
                Arrays.stream(Priority.values())
                        .map(priority ->
                                (Supplier<Optional<ProviderEntry>>) () ->
                                        providers.get(priority).stream()
                                                .filter(entry -> entry.predicate.test(field))
                                                .findFirst()
                        )
        )
                .map(entry -> entry.provider.get(i18n, field, config, defaults, registry))
                .orElse(null);
        
        if (guis != null) {
            DependencyManager dependencies = registry.getDependencyManager();
            guis.forEach(gui -> dependencies.register(gui, field));
        }
        
        return guis;
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
        List<GuiTransformer> matchedTransformers = this.transformers.stream()
                .filter(entry -> entry.predicate.test(field))
                .map(entry -> entry.transformer)
                .collect(Collectors.toList());
        
        for (GuiTransformer transformer : matchedTransformers) {
            guis = transformer.transform(guis, i18n, field, config, defaults, registry);
        }
    
        if (guis != null) {
            DependencyManager dependencies = registry.getDependencyManager();
            guis.forEach(gui -> dependencies.register(gui, field));
        }
        
        return guis;
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
    
    private enum Priority {
        FIRST,
        NORMAL,
        LAST
    }
    
    private static class ProviderEntry {
        final Predicate<Field> predicate;
        final GuiProvider provider;
        
        ProviderEntry(Predicate<Field> predicate, GuiProvider provider) {
            this.predicate = predicate;
            this.provider = provider;
        }
    }
    
    private static class TransformerEntry {
        final Predicate<Field> predicate;
        final GuiTransformer transformer;
        
        TransformerEntry(Predicate<Field> predicate, GuiTransformer transformer) {
            this.predicate = predicate;
            this.transformer = transformer;
        }
    }
}
