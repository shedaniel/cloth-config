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

import me.shedaniel.clothconfig2.impl.EasingMethod;
import net.minecraft.Util;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class DoubleValueAnimatorImpl extends NumberAnimator<Double> {
    private double amount;
    private double target;
    private long start;
    private long duration;
    
    DoubleValueAnimatorImpl() {
    }
    
    DoubleValueAnimatorImpl(double amount) {
        setAs(amount);
    }
    
    @Override
    public NumberAnimator<Double> setToNumber(Number value, long duration) {
        double doubleValue = value.doubleValue();
        if (target != doubleValue) {
            this.set(doubleValue, duration);
        }
        
        return this;
    }
    
    @Override
    public NumberAnimator<Double> setTargetNumber(Number value) {
        if (duration == 0) {
            this.setAsNumber(value);
        } else {
            this.target = value.doubleValue();
        }
        return this;
    }
    
    private void set(double value, long duration) {
        this.target = value;
        this.start = Util.getMillis();
        
        if (duration > 0) {
            this.duration = duration;
        } else {
            this.duration = 0;
            this.amount = this.target;
        }
    }
    
    @Override
    public void update(double delta) {
        if (duration != 0) {
            if (amount < target) {
                this.amount = Math.min(ease(amount, target + (target - amount), Math.min(((double) Util.getMillis() - start) / duration * delta * 3.0D, 1.0D), EasingMethod.EasingMethodImpl.LINEAR), target);
            } else if (amount > target) {
                this.amount = Math.max(ease(amount, target - (amount - target), Math.min(((double) Util.getMillis() - start) / duration * delta * 3.0D, 1.0D), EasingMethod.EasingMethodImpl.LINEAR), target);
            }
        }
    }
    
    private static double ease(double start, double end, double amount, EasingMethod easingMethod) {
        return start + (end - start) * easingMethod.apply(amount);
    }
    
    @Override
    public int intValue() {
        return (int) amount;
    }
    
    @Override
    public long longValue() {
        return (long) amount;
    }
    
    @Override
    public float floatValue() {
        return (float) amount;
    }
    
    @Override
    public double doubleValue() {
        return amount;
    }
    
    public Double target() {
        return target;
    }
    
    @Override
    public Double value() {
        return amount;
    }
}
