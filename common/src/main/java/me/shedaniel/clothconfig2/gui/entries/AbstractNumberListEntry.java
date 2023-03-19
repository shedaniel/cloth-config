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

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractNumberListEntry<T extends Number> extends TextFieldListEntry<T> {
    private static final Function<String, String> stripCharacters = s -> {
        StringBuilder builder = new StringBuilder();
        char[] chars = s.toCharArray();
        for (char c : chars)
            if (Character.isDigit(c) || c == '-' || c == '.')
                builder.append(c);
        
        return builder.toString();
    };
    protected T minimum, maximum;
 
    @ApiStatus.Internal
    @Deprecated
    protected AbstractNumberListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue) {
        super(fieldName, original, resetButtonKey, defaultValue);
        applyDefaultRange();
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected AbstractNumberListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier) {
        super(fieldName, original, resetButtonKey, defaultValue, tooltipSupplier);
        applyDefaultRange();
    }
    
    @ApiStatus.Internal
    @Deprecated
    protected AbstractNumberListEntry(Component fieldName, T original, Component resetButtonKey, Supplier<T> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, original, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
        applyDefaultRange();
    }
    
    protected abstract Map.Entry<T, T> getDefaultRange();
    
    private void applyDefaultRange() {
        Map.Entry<T, T> range = getDefaultRange();
    
        if (range != null) {
            this.minimum = range.getKey();
            this.maximum = range.getValue();
        }
    }
    
    @Override
    protected String stripAddText(String s) {
        return stripCharacters.apply(s);
    }
}
