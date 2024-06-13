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
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

@Environment(EnvType.CLIENT)
public class ConfigScreenProvider<T extends ConfigData> implements Supplier<Screen> {
    
    private static final ResourceLocation TRANSPARENT_BACKGROUND = ResourceLocation.parse(Config.Gui.Background.TRANSPARENT);
    
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
        
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable(String.format("%s.title", i18n))).setSavingRunnable(manager::save);
        
        Class<T> configClass = manager.getConfigClass();
        
        if (configClass.isAnnotationPresent(Config.Gui.Background.class)) {
            String bg = configClass.getAnnotation(Config.Gui.Background.class).value();
            ResourceLocation bgId = ResourceLocation.tryParse(bg);
            if (TRANSPARENT_BACKGROUND.equals(bgId))
                builder.transparentBackground().setDefaultBackgroundTexture(null);
            else
                builder.solidBackground().setDefaultBackgroundTexture(bgId);
        }
        
        Map<String, ResourceLocation> categoryBackgrounds =
                Arrays.stream(configClass.getAnnotationsByType(Config.Gui.CategoryBackground.class))
                        .collect(
                                toMap(
                                        Config.Gui.CategoryBackground::category,
                                        ann -> ResourceLocation.parse(ann.background())
                                )
                        );
        
        Arrays.stream(configClass.getDeclaredFields())
                .collect(
                        groupingBy(
                                field -> getOrCreateCategoryForField(field, builder, categoryBackgrounds, i18n),
                                LinkedHashMap::new,
                                toList()
                        )
                )
                .forEach(
                        (key, value) -> value.forEach(
                                field -> {
                                    String optionI13n = optionFunction.apply(i18n, field);
                                    registry.getAndTransform(optionI13n, field, config, defaults, registry)
                                            .forEach(key::addEntry);
                                }
                        )
                );
        
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
}
