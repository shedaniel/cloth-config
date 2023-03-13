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

package me.shedaniel.autoconfig.gui;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.DependsOn;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.DependsOnGroup;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.dependencies.BooleanDependency;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.SelectionDependency;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

@Environment(EnvType.CLIENT)
public class ConfigScreenProvider<T extends ConfigData> implements Supplier<Screen> {
    
    private static final ResourceLocation TRANSPARENT_BACKGROUND = new ResourceLocation(Config.Gui.Background.TRANSPARENT);
    
    private final ConfigManager<T> manager;
    private final GuiRegistryAccess registry;
    private final Screen parent;
    private Function<ConfigManager<T>, String> i18nFunction = manager -> String.format("text.autoconfig.%s", manager.getDefinition().name());
    private Function<ConfigBuilder, Screen> buildFunction = ConfigBuilder::build;
    private BiFunction<String, Field, String> optionFunction = (baseI13n, field) -> String.format("%s.option.%s", baseI13n, field.getName());
    private BiFunction<String, String, String> categoryFunction = (baseI13n, categoryName) -> String.format("%s.category.%s", baseI13n, categoryName);
    
    public ConfigScreenProvider(
            ConfigManager<T> manager,
            GuiRegistryAccess registry,
            Screen parent
    ) {
        this.manager = manager;
        this.registry = registry;
        this.parent = parent;
    }
    
    @Deprecated
    public void setI13nFunction(Function<ConfigManager<T>, String> i18nFunction) {
        this.i18nFunction = i18nFunction;
    }
    
    @Deprecated
    public void setBuildFunction(Function<ConfigBuilder, Screen> buildFunction) {
        this.buildFunction = buildFunction;
    }
    
    @Deprecated
    public void setCategoryFunction(BiFunction<String, String, String> categoryFunction) {
        this.categoryFunction = categoryFunction;
    }
    
    @Deprecated
    public void setOptionFunction(BiFunction<String, Field, String> optionFunction) {
        this.optionFunction = optionFunction;
    }
    
    @Override
    public Screen get() {
        T config = manager.getConfig();
        T defaults = manager.getSerializer().createDefault();
        
        String i18n = i18nFunction.apply(manager);
    
        // Keep references to all GUI entries in this map.
        // So that we can access them later when adding dependencies
        Map<String, AbstractConfigListEntry<?>> i18nMap = new HashMap<>();
        
        // Function to build a Dependency from a DependsOn annotation, using i18nMao
        Function<DependsOn, Dependency> buildDependsOn = (annotation) -> {
            String dependencyI18n = annotation.value();
            AbstractConfigListEntry<?> dependency = i18nMap.get(dependencyI18n);
            if (dependency == null)
                throw new RuntimeException("Specified dependency not found: \"%s\"".formatted(dependencyI18n));
        
            return buildDependency(annotation, dependency);
        };
    
        // Function to build a DependencyGroup from a DependsOnGroup annotation
        Function<DependsOnGroup, Dependency> buildDependsOnGroup = (annotation) -> {
            // Build each dependency as defined in DependsOn annotations
            Dependency[] dependencies = Arrays.stream(annotation.dependencies())
                    .map(buildDependsOn)
                    .toArray(Dependency[]::new);
        
            // Return the appropriate DependencyGroup variant
            return switch (annotation.value()) {
                case ALL -> Dependency.all(dependencies);
                case NONE -> Dependency.none(dependencies);
                case ANY -> Dependency.any(dependencies);
                case ONE -> Dependency.one(dependencies);
            };
        };
        
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable(String.format("%s.title", i18n))).setSavingRunnable(manager::save);
        
        Class<T> configClass = manager.getConfigClass();
        
        if (configClass.isAnnotationPresent(Config.Gui.Background.class)) {
            String bg = configClass.getAnnotation(Config.Gui.Background.class).value();
            ResourceLocation bgId = ResourceLocation.tryParse(bg);
            if (TRANSPARENT_BACKGROUND.equals(bgId))
                builder.transparentBackground();
            else
                builder.setDefaultBackgroundTexture(bgId);
        }
        
        Map<String, ResourceLocation> categoryBackgrounds =
                Arrays.stream(configClass.getAnnotationsByType(Config.Gui.CategoryBackground.class))
                        .collect(toMap(Config.Gui.CategoryBackground::category,
                                       ann -> new ResourceLocation(ann.background())));
    
        Map<ConfigCategory, List<Field>> categoryMap = Arrays.stream(configClass.getDeclaredFields())
                .collect(groupingBy(
                        field -> getOrCreateCategoryForField(field, builder, categoryBackgrounds, i18n),
                        LinkedHashMap::new,
                        toList()
                ));
        
        // - Iterate through each category's fields
        // - Transform each field into config entries
        // - Add the entry to the GUI
        categoryMap.forEach((category, fields) -> fields.forEach(field -> {
            String optionI18n = optionFunction.apply(i18n, field);
            registry.getAndTransform(optionI18n, field, config, defaults, registry)
                    .forEach(entry -> {
                        category.addEntry(entry);
                        i18nMap.put(optionI18n, entry);
                    });
        }));
        
        // Iterate through each category's fields again.
        // This time, look for fields that have defined dependencies and generate them.
        // Apply the generated dependencies to the config entries referenced by the i18n map.
        categoryMap.forEach((category, fields) -> fields.stream()
                .filter(field -> field.isAnnotationPresent(DependsOn.class) || field.isAnnotationPresent(DependsOnGroup.class))
                .forEach(field -> {
                    String optionI18n = optionFunction.apply(i18n, field);
                    AbstractConfigListEntry<?> entry = i18nMap.get(optionI18n);
                    if (entry == null)
                        throw new IllegalStateException("Specified entry not found: \"%s\"".formatted(optionI18n));
    
                    Dependency dependency;
                    if (field.isAnnotationPresent(DependsOnGroup.class)) {
                        dependency = buildDependsOnGroup.apply(field.getAnnotation(DependsOnGroup.class));
                    } else if (field.isAnnotationPresent(DependsOn.class)) {
                        dependency = buildDependsOn.apply(field.getAnnotation(DependsOn.class));
                    } else {
                        throw new RuntimeException("Neither DependsOn nor DependsOnGroup annotation is present.");
                    }

                    entry.setDependency(dependency);
                }));

        return buildFunction.apply(builder);
    }
    
    private ConfigCategory getOrCreateCategoryForField(
            Field field,
            ConfigBuilder screenBuilder,
            Map<String, ResourceLocation> backgroundMap,
            String baseI13n
    ) {
        String categoryName = "default";
        
        if (field.isAnnotationPresent(ConfigEntry.Category.class))
            categoryName = field.getAnnotation(ConfigEntry.Category.class).value();
        
        Component categoryKey = Component.translatable(categoryFunction.apply(baseI13n, categoryName));
        
        if (!screenBuilder.hasCategory(categoryKey)) {
            ConfigCategory category = screenBuilder.getOrCreateCategory(categoryKey);
            if (backgroundMap.containsKey(categoryName)) {
                category.setCategoryBackground(backgroundMap.get(categoryName));
            }
            return category;
        }
        
        return screenBuilder.getOrCreateCategory(categoryKey);
    }
    
    /**
     * Build a {@link Dependency} on the config entry, as defined in the annotation.
     * <br><br>
     * Currently, supports {@link BooleanListEntry} and {@link SelectionListEntry} dependencies.
     * If a different config entry type is used, an {@link IllegalStateException} will be thrown.
     * 
     * @param annotation The {@link DependsOn} annotation defining the dependency
     * @param dependency The depended-on {@link AbstractConfigListEntry}
     * @return The built {@link Dependency}
     * @throws IllegalStateException when an unsupported dependency type is used, or the annotation is somehow invalid
     */
    private static Dependency buildDependency(DependsOn annotation, AbstractConfigListEntry<?> dependency) throws IllegalStateException {
        if (dependency instanceof BooleanListEntry booleanListEntry) {
            return buildDependency(annotation, booleanListEntry);
        } else if (dependency instanceof SelectionListEntry<?> selectionListEntry) {
            return buildDependency(annotation, selectionListEntry);
        } else {
            throw new IllegalStateException("Unsupported dependency type: %s".formatted(dependency.getClass().getSimpleName()));
        }
    }
    
    private static BooleanDependency buildDependency(DependsOn annotation, BooleanListEntry dependency) {
        List<Boolean> conditions = Arrays.stream(annotation.conditions())
                // Functionally equivalent to Boolean::parseBoolean, but allows us to throw a RuntimeException
                .map(condition -> switch (condition.toLowerCase()) {
                    case "true" -> true;
                    case "false" -> false;
                    default -> throw new IllegalStateException("Unexpected condition \"%s\" for Boolean dependency (expected \"true\" or \"false\").".formatted(condition));
                })
                .toList();
        
        if (conditions.size() != 1)
            throw new IllegalStateException("Boolean dependencies require exactly one condition, found " + conditions.size());
    
        // Finally, build the dependency and return it
        BooleanDependency booleanDependency = Dependency.disabledWhenNotMet(dependency, conditions.get(0));
        booleanDependency.hiddenWhenNotMet(annotation.hiddenWhenNotMet());
        
        return booleanDependency;
    }
    
    private static <T> SelectionDependency<T> buildDependency(DependsOn annotation, SelectionListEntry<T> dependency) {
        // List of valid values for the depended-on SelectionListEntry
        List<T> possibleValues = dependency.getValues();

        // Convert each condition to the appropriate type, by
        // mapping the dependency conditions to matched possible values
        List<T> conditions = Arrays.stream(annotation.conditions())
                .map(condition -> possibleValues.stream()
                        .filter(value -> value.toString().equalsIgnoreCase(condition))
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("Invalid SelectionDependency condition was defined: \"%s\"\nValid options: %s".formatted(condition, possibleValues))))
                .toList();
    
        // Check enough conditions were parsed
        if (conditions.isEmpty())
            throw new IllegalStateException("SelectionList dependency requires at least one condition");
    
        // Finally, build the dependency and return it
        SelectionDependency<T> selectionDependency = Dependency.disabledWhenNotMet(dependency, conditions.get(0));
        if (conditions.size() > 1)
            selectionDependency.addConditions(conditions.subList(1, conditions.size()));
        selectionDependency.hiddenWhenNotMet(annotation.hiddenWhenNotMet());
        
        return selectionDependency;
    }
}
