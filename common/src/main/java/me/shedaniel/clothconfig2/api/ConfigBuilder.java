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

package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.ConfigBuilderImpl;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface ConfigBuilder {
    
    static ConfigBuilder create() {
        return new ConfigBuilderImpl();
    }
    
    ConfigBuilder setFallbackCategory(ConfigCategory fallbackCategory);
    
    Screen getParentScreen();
    
    ConfigBuilder setParentScreen(Screen parent);
    
    Component getTitle();
    
    ConfigBuilder setTitle(Component title);
    
    boolean isEditable();
    
    ConfigBuilder setEditable(boolean editable);
    
    ConfigCategory getOrCreateCategory(Component categoryKey);
    
    ConfigBuilder removeCategory(Component categoryKey);
    
    ConfigBuilder removeCategoryIfExists(Component categoryKey);
    
    boolean hasCategory(Component category);
    
    ConfigBuilder setShouldTabsSmoothScroll(boolean shouldTabsSmoothScroll);
    
    boolean isTabsSmoothScrolling();
    
    ConfigBuilder setShouldListSmoothScroll(boolean shouldListSmoothScroll);
    
    boolean isListSmoothScrolling();
    
    ConfigBuilder setDoesConfirmSave(boolean confirmSave);
    
    boolean doesConfirmSave();
    
    /**
     * This feature has been removed.
     */
    @Deprecated
    default ConfigBuilder setDoesProcessErrors(boolean processErrors) {
        return this;
    }
    
    /**
     * This feature has been removed.
     */
    @Deprecated
    default boolean doesProcessErrors() {
        return false;
    }
    
    ResourceLocation getDefaultBackgroundTexture();
    
    ConfigBuilder setDefaultBackgroundTexture(ResourceLocation texture);
    
    Runnable getSavingRunnable();
    
    ConfigBuilder setSavingRunnable(Runnable runnable);
    
    Consumer<Screen> getAfterInitConsumer();
    
    ConfigBuilder setAfterInitConsumer(Consumer<Screen> afterInitConsumer);
    
    default ConfigBuilder alwaysShowTabs() {
        return setAlwaysShowTabs(true);
    }
    
    void setGlobalized(boolean globalized);
    
    void setGlobalizedExpanded(boolean globalizedExpanded);
    
    boolean isAlwaysShowTabs();
    
    ConfigBuilder setAlwaysShowTabs(boolean alwaysShowTabs);
    
    ConfigBuilder setTransparentBackground(boolean transparentBackground);
    
    default ConfigBuilder transparentBackground() {
        return setTransparentBackground(true);
    }
    
    default ConfigBuilder solidBackground() {
        return setTransparentBackground(false);
    }
    
    @Deprecated
    default ConfigEntryBuilderImpl getEntryBuilder() {
        return (ConfigEntryBuilderImpl) entryBuilder();
    }
    
    default ConfigEntryBuilder entryBuilder() {
        return ConfigEntryBuilderImpl.create();
    }
    
    Screen build();
    
    boolean hasTransparentBackground();
}
