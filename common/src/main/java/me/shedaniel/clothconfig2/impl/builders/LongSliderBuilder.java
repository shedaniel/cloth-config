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

import me.shedaniel.clothconfig2.gui.entries.LongSliderEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class LongSliderBuilder extends AbstractSliderFieldBuilder<Long, LongSliderEntry, LongSliderBuilder> {
    
    public LongSliderBuilder(Component resetButtonKey, Component fieldNameKey, long value, long min, long max) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.max = max;
        this.min = min;
    }
    
    @Override
    public LongSliderBuilder setErrorSupplier(Function<Long, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public LongSliderBuilder requireRestart() {
        return super.requireRestart();
    }
    
    @Override
    public LongSliderBuilder setTextGetter(Function<Long, Component> textGetter) {
        return super.setTextGetter(textGetter);
    }
    
    @Override
    public LongSliderBuilder setSaveConsumer(Consumer<Long> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public LongSliderBuilder setDefaultValue(Supplier<Long> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public LongSliderBuilder setDefaultValue(long defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    @Override
    public LongSliderBuilder setTooltipSupplier(Function<Long, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public LongSliderBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public LongSliderBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public LongSliderBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public LongSliderEntry build() {
        LongSliderEntry entry = new LongSliderEntry(getFieldNameKey(), min, max, value, getSaveConsumer(), getResetButtonKey(), defaultValue, null, isRequireRestart());
        if (textGetter != null)
            entry.setTextGetter(textGetter);
        entry.setTooltipSupplier(() -> getTooltipSupplier().apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return finishBuilding(entry);
    }
    
}
