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

package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleListEntry extends AbstractNumberListEntry<Double> {
    @ApiStatus.Internal
    @Deprecated
    public DoubleListEntry(Component fieldName, Double value, Component resetButtonKey, Supplier<Double> defaultValue, Consumer<Double> saveConsumer) {
        super(fieldName, value, resetButtonKey, defaultValue);
        this.saveCallback = saveConsumer;
    }
    
    @ApiStatus.Internal
    @Deprecated
    public DoubleListEntry(Component fieldName, Double value, Component resetButtonKey, Supplier<Double> defaultValue, Consumer<Double> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public DoubleListEntry(Component fieldName, Double value, Component resetButtonKey, Supplier<Double> defaultValue, Consumer<Double> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, value, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
        this.saveCallback = saveConsumer;
    }
    
    @Override
    protected Map.Entry<Double, Double> getDefaultRange() {
        return new AbstractMap.SimpleEntry<>(-Double.MAX_VALUE, Double.MAX_VALUE);
    }
    
    public DoubleListEntry setMinimum(double minimum) {
        this.minimum = minimum;
        return this;
    }
    
    public DoubleListEntry setMaximum(double maximum) {
        this.maximum = maximum;
        return this;
    }
    
    @Override
    public Double getValue() {
        try {
            return Double.valueOf(textFieldWidget.getValue());
        } catch (Exception e) {
            return 0d;
        }
    }
    
    @Override
    public Optional<Component> getError() {
        try {
            double i = Double.parseDouble(textFieldWidget.getValue());
            if (i > maximum)
                return Optional.of(Component.translatable("text.cloth-config.error.too_large", maximum));
            else if (i < minimum)
                return Optional.of(Component.translatable("text.cloth-config.error.too_small", minimum));
        } catch (NumberFormatException ex) {
            return Optional.of(Component.translatable("text.cloth-config.error.not_valid_number_double"));
        }
        return super.getError();
    }
}
