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

package me.shedaniel.clothconfig2;

import me.shedaniel.clothconfig2.api.ScrollingContainer;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.clothconfig2.impl.EasingMethod.EasingMethodImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public class ClothConfigInitializer {
    public static final Logger LOGGER = LogManager.getFormatterLogger("ClothConfig");
    
    public static final String MOD_ID = "cloth_config";
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double handleScrollingPosition(double[] target, double scroll, double maxScroll, float delta, double start, double duration) {
        return ScrollingContainer.handleScrollingPosition(target, scroll, maxScroll, delta, start, duration);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double expoEase(double start, double end, double amount) {
        return ScrollingContainer.ease(start, end, amount, getEasingMethod());
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double clamp(double v, double maxScroll) {
        return ScrollingContainer.clampExtension(v, maxScroll);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double clamp(double v, double maxScroll, double clampExtension) {
        return ScrollingContainer.clampExtension(v, -clampExtension, maxScroll + clampExtension);
    }
    
    public static EasingMethod getEasingMethod() {
        return EasingMethodImpl.NONE;
    }
    
    public static long getScrollDuration() {
        return 600;
    }
    
    public static double getScrollStep() {
        return 16.0;
    }
    
    public static double getBounceBackMultiplier() {
        return -10;
    }
}
