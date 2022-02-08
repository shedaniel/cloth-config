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

import java.util.Objects;
import java.util.function.Supplier;

@ApiStatus.Internal
final class ConventionValueAnimator<T> implements ValueAnimator<T> {
    private final ValueAnimator<T> parent;
    private final Supplier<T> convention;
    private final long duration;
    
    ConventionValueAnimator(ValueAnimator<T> parent, Supplier<T> convention, long duration) {
        this.parent = parent;
        this.convention = convention;
        this.duration = duration;
        setAs(convention.get());
    }
    
    @Override
    public ValueAnimator<T> setTo(T value, long duration) {
        return parent.setTo(value, duration);
    }
    
    @Override
    public ValueAnimator<T> setTarget(T target) {
        return parent.setTarget(target);
    }
    
    @Override
    public T target() {
        return convention.get();
    }
    
    @Override
    public T value() {
        return parent.value();
    }
    
    @Override
    public void update(double delta) {
        parent.update(delta);
        T target = target();
        if (!Objects.equals(parent.target(), target)) {
            setTo(target, duration);
        }
    }
}
