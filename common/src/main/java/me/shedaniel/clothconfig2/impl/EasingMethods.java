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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EasingMethods {
    private static final List<EasingMethod> METHODS;
    
    static {
        METHODS = new ArrayList<>();
        METHODS.addAll(Arrays.asList(EasingMethod.EasingMethodImpl.values()));
    }
    
    public static void register(EasingMethod easingMethod) {
        METHODS.add(easingMethod);
    }
    
    public static List<EasingMethod> getMethods() {
        return Collections.unmodifiableList(METHODS);
    }
}
