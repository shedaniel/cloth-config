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

package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleFieldBuilder extends FieldBuilder<Double, DoubleListEntry, DoubleFieldBuilder> {
    private final double value;
    private Double min = null, max = null;
    
    public DoubleFieldBuilder(Component resetButtonKey, Component fieldNameKey, double value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public DoubleFieldBuilder setErrorSupplier(Function<Double, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public DoubleFieldBuilder requireRestart() {
        return super.requiresRestart();
    }
    
    public DoubleFieldBuilder setSaveConsumer(Consumer<Double> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    public DoubleFieldBuilder setDefaultValue(Supplier<Double> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public DoubleFieldBuilder setDefaultValue(double defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public DoubleFieldBuilder setMin(double min) {
        this.min = min;
        return this;
    }
    
    public DoubleFieldBuilder setMax(double max) {
        this.max = max;
        return this;
    }
    
    public DoubleFieldBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public DoubleFieldBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public DoubleFieldBuilder setTooltipSupplier(Function<Double, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public DoubleFieldBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public DoubleFieldBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    public DoubleFieldBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public DoubleListEntry build() {
        DoubleListEntry entry = new DoubleListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
