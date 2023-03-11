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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleFieldBuilder extends AbstractRangeFieldBuilder<Double, DoubleListEntry, DoubleFieldBuilder> {
    
    public DoubleFieldBuilder(Component resetButtonKey, Component fieldNameKey, double value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public DoubleFieldBuilder setErrorSupplier(Function<Double, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public DoubleFieldBuilder requireRestart() {
        return super.requireRestart();
    }
    
    @Override
    public DoubleFieldBuilder setSaveConsumer(Consumer<Double> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public DoubleFieldBuilder setDefaultValue(Supplier<Double> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public DoubleFieldBuilder setDefaultValue(double defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public DoubleFieldBuilder setMin(double min) {
        this.min = min;
        return this;
    }
    
    public DoubleFieldBuilder setMax(double max) {
        this.max = max;
        return this;
    }
    
    @Override
    public DoubleFieldBuilder removeMin() {
        return super.removeMin();
    }
    
    @Override
    public DoubleFieldBuilder removeMax() {
        return super.removeMax();
    }
    
    @Override
    public DoubleFieldBuilder setTooltipSupplier(Function<Double, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public DoubleFieldBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public DoubleFieldBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public DoubleFieldBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public DoubleListEntry build() {
        DoubleListEntry entry = new DoubleListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, getSaveConsumer(), null, isRequireRestart());
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        entry.setTooltipSupplier(() -> getTooltipSupplier().apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        if (!dependencies.isEmpty())
            entry.addDependencies(dependencies);
        return entry;
    }
    
}
