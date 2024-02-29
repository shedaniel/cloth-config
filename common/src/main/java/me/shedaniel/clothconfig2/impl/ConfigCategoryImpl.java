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

package me.shedaniel.clothconfig2.impl;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ConfigCategoryImpl implements ConfigCategory {
    private final ConfigBuilder builder;
    private final List<Object> data;
    @Nullable
    private ResourceLocation background;
    private final Component categoryKey;
    @Nullable
    private Supplier<Optional<FormattedText[]>> description = Optional::empty;
    
    ConfigCategoryImpl(ConfigBuilder builder, Component categoryKey) {
        this.builder = builder;
        this.data = Lists.newArrayList();
        this.categoryKey = categoryKey;
    }
    
    @Override
    public Component getCategoryKey() {
        return categoryKey;
    }
    
    @Override
    public List<Object> getEntries() {
        return data;
    }
    
    @Override
    public ConfigCategory addEntry(AbstractConfigListEntry entry) {
        data.add(entry);
        return this;
    }
    
    @Override
    public ConfigCategory setCategoryBackground(ResourceLocation identifier) {
        background = identifier;
        return this;
    }
    
    @Override
    public void removeCategory() {
        builder.removeCategory(categoryKey);
    }
    
    @Override
    public void setBackground(@Nullable ResourceLocation background) {
        this.background = background;
    }
    
    @Override
    @Nullable
    public ResourceLocation getBackground() {
        return background;
    }
    
    @Nullable
    @Override
    public Supplier<Optional<FormattedText[]>> getDescription() {
        return description;
    }
    
    @Override
    public void setDescription(@Nullable Supplier<Optional<FormattedText[]>> description) {
        this.description = description;
    }
}
