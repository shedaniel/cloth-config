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

import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ComposedGuiRegistryAccess implements GuiRegistryAccess {
    
    private List<GuiRegistryAccess> children;
    
    public ComposedGuiRegistryAccess(GuiRegistryAccess... children) {
        this.children = Arrays.asList(children);
    }
    
    @Override
    public List<AbstractConfigListEntry> get(
            String i18n,
            Field field,
            Object config,
            Object defaults,
            GuiRegistryAccess registry) {
        return children.stream()
                .map(child -> child.get(i18n, field, config, defaults, registry))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No ConfigGuiProvider match!"));
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
        return children.stream()
                .reduce(guis,
                        (prevResult, child) -> child.transform(prevResult, i18n, field, config, defaults, registry),
                        (a, b) -> { throw new UnsupportedOperationException("Cannot transform GUIs in parallel!"); });
    }
    
    @Override
    public void runPreHook(String i18n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        children.forEach(child -> child.runPreHook(i18n, field, config, defaults, registry));
    }
    
    @Override
    public void runPostHook(List<AbstractConfigListEntry> guis, String i18n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        children.forEach(child -> child.runPostHook(guis, i18n, field, config, defaults, registry));
    }
}
