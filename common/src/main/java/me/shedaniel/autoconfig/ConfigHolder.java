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

package me.shedaniel.autoconfig;

import me.shedaniel.autoconfig.event.ConfigSerializeEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface ConfigHolder<T extends ConfigData> extends Supplier<T> {
    @NotNull
    Class<T> getConfigClass();

    void save();
    
    boolean load();
    
    T getConfig();
    
    void registerSaveListener(ConfigSerializeEvent.Save<T> save);
    
    void registerLoadListener(ConfigSerializeEvent.Load<T> load);
    
    @Override
    default T get() {
        return getConfig();
    }

    /**
     * Resets the config held by this holder to its default values.
     * <br>
     * Does not save the reset config to file, for that use {@link #save()}.
     */
    void resetToDefault();

    /**
     * Sets the config held by this holder.
     * <br>
     * Does not save the set config to file, for that use {@link #save()}.
     */
    void setConfig(T config);
}
