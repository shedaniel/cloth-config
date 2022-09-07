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
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FloatListEntry extends AbstractNumberListEntry<Float> {
    @ApiStatus.Internal
    @Deprecated
    public FloatListEntry(Component fieldName, Float value, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer) {
        super(fieldName, value, resetButtonKey, defaultValue);
        this.saveCallback = saveConsumer;
    }
    
    @ApiStatus.Internal
    @Deprecated
    public FloatListEntry(Component fieldName, Float value, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public FloatListEntry(Component fieldName, Float value, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, value, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
        this.saveCallback = saveConsumer;
    }
    
    @Override
    protected Map.Entry<Float, Float> getDefaultRange() {
        return new AbstractMap.SimpleEntry<>(-Float.MAX_VALUE, Float.MAX_VALUE);
    }
    
    public FloatListEntry setMinimum(float minimum) {
        this.minimum = minimum;
        return this;
    }
    
    public FloatListEntry setMaximum(float maximum) {
        this.maximum = maximum;
        return this;
    }
    
    @Override
    public Float getValue() {
        try {
            return Float.valueOf(textFieldWidget.getValue());
        } catch (Exception e) {
            return 0f;
        }
    }
    
    @Override
    public Optional<Component> getError() {
        try {
            float i = Float.parseFloat(textFieldWidget.getValue());
            if (i > maximum)
                return Optional.of(new TranslatableComponent("text.cloth-config.error.too_large", maximum));
            else if (i < minimum)
                return Optional.of(new TranslatableComponent("text.cloth-config.error.too_small", minimum));
        } catch (NumberFormatException ex) {
            return Optional.of(new TranslatableComponent("text.cloth-config.error.not_valid_number_float"));
        }
        return super.getError();
    }
}
