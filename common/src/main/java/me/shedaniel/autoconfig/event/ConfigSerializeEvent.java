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

package me.shedaniel.autoconfig.event;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.InteractionResult;

public final class ConfigSerializeEvent {
    private ConfigSerializeEvent() {}
    
    @FunctionalInterface
    public interface Save<T extends ConfigData> {
        /**
         * Callback that is called before the config manager serializes its config values
         * This is called on initialization or when the config screen is saved.
         * <p>
         * The callback uses an ActionResult to determine further actions
         * - SUCCESS stops any extra processing and uses the default behavior along
         * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
         * - FAIL cancels further processing (the equivalent of returning the method)
         * <p>
         * Also avoid calling {@link ConfigHolder#save()} in this callback, as it
         * will result in an exception
         * <p>
         */
        InteractionResult onSave(ConfigHolder<T> manager, T data);
    }
    
    @FunctionalInterface
    public interface Load<T extends ConfigData> {
        /**
         * Callback that is called after the config manager deserializes its config values
         * This is called on initialization or when the config screen is loaded.
         * <p>
         * This is also called in {@link ConfigHolder#load()} if the config file was edited
         * manually.
         * <p>
         * The callback uses an ActionResult to determine further actions
         * - SUCCESS stops any extra processing and uses the default behavior along
         * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
         * - FAIL cancels further processing (the equivalent of returning the method)
         * <p>
         * Also avoid calling {@link ConfigHolder#load()} in this callback, as it
         * will result in an exception
         */
        InteractionResult onLoad(ConfigHolder<T> manager, T newData);
    }
}
