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

package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractSliderFieldBuilder<T, A extends AbstractConfigListEntry, SELF extends FieldBuilder<T, A, SELF>> extends AbstractRangeFieldBuilder<T, A, SELF> {
    protected Function<T, Component> textGetter = null;
    
    protected AbstractSliderFieldBuilder(Component resetButtonKey, Component fieldNameKey) {
        super(resetButtonKey, fieldNameKey);
    }
    
    public SELF setTextGetter(Function<T, Component> textGetter) {
        this.textGetter = textGetter;
        return (SELF) this;
    }
    
    @Override
    public SELF setMin(T min) {
        Objects.requireNonNull(min, "min cannot be null");
        return super.setMin(min);
    }
    
    @Override
    public SELF setMax(T max) {
        Objects.requireNonNull(max, "max cannot be null");
        return super.setMax(max);
    }
}
