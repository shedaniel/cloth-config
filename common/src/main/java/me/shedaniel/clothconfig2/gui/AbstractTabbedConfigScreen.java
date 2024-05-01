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

package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Maps;
import me.shedaniel.clothconfig2.api.TabbedConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public abstract class AbstractTabbedConfigScreen extends AbstractConfigScreen implements TabbedConfigScreen {
    private final Map<String, Boolean> categoryTransparentBackground = Maps.newHashMap();
    private final Map<String, ResourceLocation> categoryBackgroundLocation = Maps.newHashMap();
    
    protected AbstractTabbedConfigScreen(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }
    
    @Override
    public final void registerCategoryBackground(String text, ResourceLocation identifier) {
        this.categoryBackgroundLocation.put(text, identifier);
    }
    
    @Override
    public void registerCategoryTransparency(String text, boolean transparent) {
        this.categoryTransparentBackground.put(text, transparent);
    }
    
    @Override
    public boolean isTransparentBackground() {
        Component selectedCategory = getSelectedCategory();
        if (categoryTransparentBackground.containsKey(selectedCategory.getString()))
            return categoryTransparentBackground.get(selectedCategory.getString());
        return super.isTransparentBackground();
    }
    
    @Override
    public ResourceLocation getBackgroundLocation() {
        Component selectedCategory = getSelectedCategory();
        if (categoryBackgroundLocation.containsKey(selectedCategory.getString()))
            return categoryBackgroundLocation.get(selectedCategory.getString());
        return super.getBackgroundLocation();
    }
}
