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

package me.shedaniel.clothconfig2.api.animator;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Internal
final class NumberAnimatorWrapped<T extends Number, R extends Number> extends NumberAnimator<T> {
    private final NumberAnimator<R> parent;
    private final Function<R, T> converter;
    
    NumberAnimatorWrapped(NumberAnimator<R> parent, Function<R, T> converter) {
        this.parent = parent;
        this.converter = converter;
    }
    
    @Override
    public NumberAnimator<T> setToNumber(Number value, long duration) {
        this.parent.setToNumber(value, duration);
        return this;
    }
    
    @Override
    public NumberAnimator<T> setTargetNumber(Number value) {
        this.parent.setTargetNumber(value);
        return this;
    }
    
    @Override
    public T target() {
        return converter.apply(parent.target());
    }
    
    @Override
    public T value() {
        return converter.apply(parent.value());
    }
    
    @Override
    public void update(double delta) {
        parent.update(delta);
    }
    
    @Override
    public int intValue() {
        return parent.intValue();
    }
    
    @Override
    public long longValue() {
        return parent.longValue();
    }
    
    @Override
    public float floatValue() {
        return parent.floatValue();
    }
    
    @Override
    public double doubleValue() {
        return parent.doubleValue();
    }
}
