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
final class MappingValueAnimator<T, R> implements ValueAnimator<R> {
    private final ValueAnimator<T> parent;
    private final Function<T, R> converter;
    private final Function<R, T> backwardsConverter;
    
    MappingValueAnimator(ValueAnimator<T> parent, Function<T, R> converter, Function<R, T> backwardsConverter) {
        this.parent = parent;
        this.converter = converter;
        this.backwardsConverter = backwardsConverter;
    }
    
    @Override
    public ValueAnimator<R> setTo(R value, long duration) {
        parent.setTo(backwardsConverter.apply(value), duration);
        return this;
    }
    
    @Override
    public ValueAnimator<R> setTarget(R target) {
        parent.setTarget(backwardsConverter.apply(target));
        return this;
    }
    
    @Override
    public R target() {
        return converter.apply(parent.target());
    }
    
    @Override
    public R value() {
        return converter.apply(parent.value());
    }
    
    @Override
    public void update(double delta) {
        parent.update(delta);
    }
}
