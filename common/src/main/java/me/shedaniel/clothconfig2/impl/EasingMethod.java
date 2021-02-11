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

package me.shedaniel.clothconfig2.impl;

import java.util.function.Function;

public interface EasingMethod {
    
    double apply(double v);
    
    enum EasingMethodImpl implements EasingMethod {
        NONE(v -> 1.0),
        LINEAR(v -> v),
        EXPO(v -> ((v == 1.0) ? 1 : 1 * (-Math.pow(2, -10 * v) + 1))),
        QUAD(v -> -1 * (v /= 1) * (v - 2)),
        QUART(v -> ((v == 1.0) ? 1 : 1 * (-1 * ((v = v - 1) * v * v * v - 1)))),
        SINE(v -> Math.sin(v * (Math.PI / 2))),
        CUBIC(v -> ((v = v - 1) * v * v + 1)),
        QUINTIC(v -> ((v = v - 1) * v * v * v * v + 1)),
        CIRC(v -> Math.sqrt(1 - (v = v - 1) * v));
        
        private final Function<Double, Double> function;
        
        EasingMethodImpl(Function<Double, Double> function) {
            this.function = function;
        }
        
        @Override
        public double apply(double v) {
            return function.apply(v);
        }
        
        @Override
        public String toString() {
            return name();
        }
    }
}
