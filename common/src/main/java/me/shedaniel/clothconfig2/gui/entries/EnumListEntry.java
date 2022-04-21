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

package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class EnumListEntry<T extends Enum<?>> extends SelectionListEntry<T> {
    
    public static final Function<Enum, Component> DEFAULT_NAME_PROVIDER = t -> Component.translatable(t instanceof Translatable ? ((Translatable) t).getKey() : t.toString());
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(Component fieldName, Class<T> clazz, T value, Component resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, DEFAULT_NAME_PROVIDER::apply);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(Component fieldName, Class<T> clazz, T value, Component resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, Component> enumNameProvider) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(Component fieldName, Class<T> clazz, T value, Component resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, Component> enumNameProvider, Supplier<Optional<Component[]>> tooltipSupplier) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public EnumListEntry(Component fieldName, Class<T> clazz, T value, Component resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<Enum, Component> enumNameProvider, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, clazz.getEnumConstants(), value, resetButtonKey, defaultValue, saveConsumer, enumNameProvider::apply, tooltipSupplier, requiresRestart);
    }
    
}
