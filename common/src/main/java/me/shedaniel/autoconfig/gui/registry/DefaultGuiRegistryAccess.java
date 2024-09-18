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
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class DefaultGuiRegistryAccess implements GuiRegistryAccess {
    @Override
    public List<AbstractConfigListEntry> get(
            String i18n,
            Field field,
            Object config,
            Object defaults,
            GuiRegistryAccess registry
    ) {
        LogManager.getLogger().error("No GUI provider registered for field '{}'!", field);
        return Collections.emptyList();
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
        return guis;
    }
    
    @Override
    public void runPreHook(String i18n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        // No-op
    }
    
    @Override
    public void runPostHook(List<AbstractConfigListEntry> guis, String i18n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        // No-op
    }
}
