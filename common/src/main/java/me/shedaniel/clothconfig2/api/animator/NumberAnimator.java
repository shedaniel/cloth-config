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

import java.util.function.Supplier;

public abstract class NumberAnimator<T extends Number> extends Number implements ValueAnimator<T> {
    public NumberAnimator<Double> asDouble() {
        return new NumberAnimatorWrapped<>(this, Number::doubleValue);
    }
    
    public NumberAnimator<Float> asFloat() {
        return new NumberAnimatorWrapped<>(this, Number::floatValue);
    }
    
    public NumberAnimator<Integer> asInt() {
        return new NumberAnimatorWrapped<>(this, d -> (int) Math.round(d.doubleValue()));
    }
    
    public NumberAnimator<Long> asLong() {
        return new NumberAnimatorWrapped<>(this, d -> Math.round(d.doubleValue()));
    }
    
    @Override
    public NumberAnimator<T> setAs(T value) {
        ValueAnimator.super.setAs(value);
        return this;
    }
    
    public NumberAnimator<T> setAs(int value) {
        setAsNumber(value);
        return this;
    }
    
    public NumberAnimator<T> setAs(long value) {
        setAsNumber(value);
        return this;
    }
    
    public NumberAnimator<T> setAs(float value) {
        setAsNumber(value);
        return this;
    }
    
    public NumberAnimator<T> setAs(double value) {
        setAsNumber(value);
        return this;
    }
    
    @Override
    public NumberAnimator<T> setTo(T value, long duration) {
        setToNumber(value, duration);
        return this;
    }
    
    public NumberAnimator<T> setTo(int value, long duration) {
        setToNumber(value, duration);
        return this;
    }
    
    public NumberAnimator<T> setTo(long value, long duration) {
        setToNumber(value, duration);
        return this;
    }
    
    public NumberAnimator<T> setTo(float value, long duration) {
        setToNumber(value, duration);
        return this;
    }
    
    public NumberAnimator<T> setTo(double value, long duration) {
        setToNumber(value, duration);
        return this;
    }
    
    public NumberAnimator<T> setAsNumber(Number value) {
        return setToNumber(value, -1);
    }
    
    public abstract NumberAnimator<T> setToNumber(Number value, long duration);
    
    @Override
    public ValueAnimator<T> setTarget(T target) {
        setTargetNumber(target);
        return this;
    }
    
    public NumberAnimator<T> setTarget(int value) {
        setTargetNumber(value);
        return this;
    }
    
    public NumberAnimator<T> setTarget(long value) {
        setTargetNumber(value);
        return this;
    }
    
    public NumberAnimator<T> setTarget(float value) {
        setTargetNumber(value);
        return this;
    }
    
    public NumberAnimator<T> setTarget(double value) {
        setTargetNumber(value);
        return this;
    }
    
    public abstract NumberAnimator<T> setTargetNumber(Number value);
    
    @Override
    public NumberAnimator<T> withConvention(Supplier<T> convention, long duration) {
        ValueAnimator<T> parentConvention = ValueAnimator.super.withConvention(convention, duration);
        return new ValueAnimatorAsNumberAnimator<T>(parentConvention) {
            @Override
            public NumberAnimator<T> setToNumber(Number value, long duration) {
                return NumberAnimator.this.setToNumber(value, duration);
            }
            
            @Override
            public NumberAnimator<T> setTargetNumber(Number value) {
                return NumberAnimator.this.setTargetNumber(value);
            }
        };
    }
}
