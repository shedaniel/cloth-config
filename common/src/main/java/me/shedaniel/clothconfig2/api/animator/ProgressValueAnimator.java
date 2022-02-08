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

@ApiStatus.Experimental
public interface ProgressValueAnimator<T> extends ValueAnimator<T> {
    double progress();
    
    @Override
    default ProgressValueAnimator<T> setAs(T value) {
        ValueAnimator.super.setAs(value);
        return this;
    }
    
    @Override
    ProgressValueAnimator<T> setTo(T value, long duration);
    
    @Override
    ProgressValueAnimator<T> setTarget(T target);
    
    static <R> ProgressValueAnimator<R> mapProgress(NumberAnimator<?> parent, Function<Double, R> converter, Function<R, Double> backwardsConverter) {
        return new MappingProgressValueAnimator<>(parent.asDouble(), converter, backwardsConverter);
    }
}
