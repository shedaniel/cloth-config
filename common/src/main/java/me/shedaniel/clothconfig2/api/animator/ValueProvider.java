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

/**
 * A value provider is used to provide a value for animation.
 *
 * @param <T> the type of the value
 * @see ValueAnimator
 */
public interface ValueProvider<T> {
    /**
     * Returns a constant value provider, which always returns the same value.
     *
     * @param value the value to return
     * @param <T>   the type of the value
     * @return the constant value provider
     */
    static <T> ValueProvider<T> constant(T value) {
        return new ConstantValueProvider<>(value);
    }
    
    /**
     * Returns the current value of the provider.
     *
     * @return the current value
     */
    T value();
    
    /**
     * Returns the target value of the provider.
     *
     * @return the target value
     */
    T target();
    
    /**
     * Completes the animation immediately.
     * This will set the current value to the target value.
     */
    void completeImmediately();
    
    /**
     * Updates the current value of the provider by the tick delta.
     *
     * @param delta the tick delta
     */
    void update(double delta);
}
