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

package me.shedaniel.clothconfig2.fabric;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.example.ExampleConfig;
import me.shedaniel.clothconfig2.ClothConfigDemo;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;

public class ClothConfigCatalogueDemo {
    public static Screen createConfigScreen(Screen currentScreen, ModContainer container) {
        if (RenderSystem.isOnRenderThread() && Screen.hasShiftDown()) return AutoConfig.getConfigScreen(ExampleConfig.class, currentScreen).get();
        return ClothConfigDemo.getConfigBuilderWithDemo().setParentScreen(currentScreen).build();
    }
}
