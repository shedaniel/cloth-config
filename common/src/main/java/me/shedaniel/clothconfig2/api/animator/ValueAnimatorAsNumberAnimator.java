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

@ApiStatus.Internal
abstract class ValueAnimatorAsNumberAnimator<T extends Number> extends NumberAnimator<T> {
    private final ValueAnimator<T> animator;
    
    ValueAnimatorAsNumberAnimator(ValueAnimator<T> animator) {
        this.animator = animator;
    }
    
    @Override
    public int intValue() {
        return animator.value().intValue();
    }
    
    @Override
    public long longValue() {
        return animator.value().longValue();
    }
    
    @Override
    public float floatValue() {
        return animator.value().floatValue();
    }
    
    @Override
    public double doubleValue() {
        return animator.value().doubleValue();
    }
    
    @Override
    public T value() {
        return animator.value();
    }
    
    @Override
    public T target() {
        return animator.target();
    }
    
    @Override
    public void update(double delta) {
        animator.update(delta);
    }
}
