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

import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
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
public class IntSliderBuilder extends FieldBuilder<Integer, IntegerSliderEntry, IntSliderBuilder> {
    private final int value;
    private int max;
    private int min;
    private Function<Integer, Component> textGetter = null;
    
    public IntSliderBuilder(Component resetButtonKey, Component fieldNameKey, int value, int min, int max) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.max = max;
        this.min = min;
    }
    
    public IntSliderBuilder setErrorSupplier(Function<Integer, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public IntSliderBuilder requireRestart() {
        return super.requiresRestart();
    }
    
    public IntSliderBuilder setTextGetter(Function<Integer, Component> textGetter) {
        this.textGetter = textGetter;
        return this;
    }
    
    public IntSliderBuilder setSaveConsumer(Consumer<Integer> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    public IntSliderBuilder setDefaultValue(Supplier<Integer> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public IntSliderBuilder setDefaultValue(int defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public IntSliderBuilder setTooltipSupplier(Function<Integer, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public IntSliderBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public IntSliderBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public IntSliderBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public IntSliderBuilder setMax(int max) {
        this.max = max;
        return this;
    }
    
    public IntSliderBuilder setMin(int min) {
        this.min = min;
        return this;
    }
    
    @NotNull
    @Override
    public IntegerSliderEntry build() {
        IntegerSliderEntry entry = new IntegerSliderEntry(getFieldNameKey(), min, max, value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        if (textGetter != null)
            entry.setTextGetter(textGetter);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
