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

import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class IntFieldBuilder extends AbstractRangeFieldBuilder<Integer, IntegerListEntry, IntFieldBuilder> {
    public IntFieldBuilder(Component resetButtonKey, Component fieldNameKey, int value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public IntFieldBuilder requireRestart() {
        return super.requireRestart();
    }
    
    @Override
    public IntFieldBuilder setErrorSupplier(Function<Integer, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public IntFieldBuilder setSaveConsumer(Consumer<Integer> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public IntFieldBuilder setDefaultValue(Supplier<Integer> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public IntFieldBuilder setDefaultValue(int defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public IntFieldBuilder setTooltipSupplier(Function<Integer, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public IntFieldBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public IntFieldBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public IntFieldBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    public IntFieldBuilder setMin(int min) {
        return super.setMin(min);
    }
    
    public IntFieldBuilder setMax(int max) {
        return super.setMax(max);
    }
    
    @Override
    public IntFieldBuilder removeMin() {
        return super.removeMin();
    }
    
    @Override
    public IntFieldBuilder removeMax() {
        return super.removeMax();
    }
    
    @NotNull
    @Override
    public IntegerListEntry build() {
        IntegerListEntry entry = new IntegerListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, getSaveConsumer(), null, isRequireRestart());
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        entry.setTooltipSupplier(() -> getTooltipSupplier().apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        if (dependency != null)
            entry.setDependency(dependency, dependantValue);
        return entry;
    }
    
}
