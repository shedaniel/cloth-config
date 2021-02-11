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

package me.shedaniel.autoconfig.example;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.DummyConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ExampleInits {
    public static void exampleCommonInit() {
        // how to register a config:
        ConfigHolder<ExampleConfig> holder = AutoConfig.register(
                ExampleConfig.class,
                PartitioningSerializer.wrap(DummyConfigSerializer::new)
        );
        
        // how to read a config:
        holder.getConfig();
        // or (please cache this value, and listen to load to re-cache)
        AutoConfig.getConfigHolder(ExampleConfig.class).getConfig();
        // this event allows you to change or register specific listeners
        // for when the config has changed
        AutoConfig.getConfigHolder(ExampleConfig.class).registerSaveListener((manager, data) -> {
            return InteractionResult.SUCCESS;
        });
        AutoConfig.getConfigHolder(ExampleConfig.class).registerLoadListener((manager, newData) -> {
            return InteractionResult.SUCCESS;
        });
    }
    
    @Environment(EnvType.CLIENT)
    public static void exampleClientInit() {
        // how to get the gui registry for custom gui handlers
        AutoConfig.getGuiRegistry(ExampleConfig.class);
    }
}
