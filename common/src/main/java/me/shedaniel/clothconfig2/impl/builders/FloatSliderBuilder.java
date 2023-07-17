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

import me.shedaniel.clothconfig2.gui.entries.FloatSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FloatSliderBuilder extends AbstractSliderFieldBuilder<Float, FloatSliderEntry, FloatSliderBuilder> {
    private int precision;
    
    public FloatSliderBuilder(Component resetButtonKey, Component fieldNameKey, float value, float min, float max, int precision) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.max = max;
        this.min = min;
        this.precision = precision;
    }
    
    @Override
    public FloatSliderBuilder setErrorSupplier(Function<Float, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public FloatSliderBuilder requireRestart() {
        return super.requireRestart();
    }
    
    @Override
    public FloatSliderBuilder setTextGetter(Function<Float, Component> textGetter) {
        return super.setTextGetter(textGetter);
    }
    
    @Override
    public FloatSliderBuilder setSaveConsumer(Consumer<Float> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public FloatSliderBuilder setDefaultValue(Supplier<Float> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public FloatSliderBuilder setDefaultValue(float defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    @Override
    public FloatSliderBuilder setTooltipSupplier(Function<Float, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public FloatSliderBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public FloatSliderBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public FloatSliderBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    public FloatSliderBuilder setMax(float max) {
        this.max = max;
        return this;
    }
    
    public FloatSliderBuilder setMin(float min) {
        this.min = min;
        return this;
    }
    
    public FloatSliderBuilder setPrecision(int precision) {
        this.precision = precision;
        return this;
    }
    
    @Override
    public FloatSliderBuilder removeMin() {
        return this;
    }
    
    @Override
    public FloatSliderBuilder removeMax() {
        return this;
    }
    
    @NotNull
    @Override
    public FloatSliderEntry build() {
        FloatSliderEntry entry = new FloatSliderEntry(getFieldNameKey(), min, max, value, precision, getResetButtonKey(), defaultValue, getSaveConsumer(), null, isRequireRestart());
        if (textGetter != null)
            entry.setTextGetter(textGetter);
        entry.setTooltipSupplier(() -> getTooltipSupplier().apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return finishBuilding(entry);
    }    
}
