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

import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ColorFieldBuilder extends FieldBuilder<Integer, ColorEntry, ColorFieldBuilder> {
    private final int value;
    private boolean alpha = false;
    
    public ColorFieldBuilder(Component resetButtonKey, Component fieldNameKey, int value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public ColorFieldBuilder setErrorSupplier(Function<Integer, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public ColorFieldBuilder requireRestart() {
        return super.requiresRestart();
    }
    
    public ColorFieldBuilder setSaveConsumer(Consumer<Integer> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    public ColorFieldBuilder setSaveConsumer2(Consumer<Color> saveConsumer) {
        this.saveConsumer = integer -> saveConsumer.accept(alpha ? Color.ofTransparent(integer) : Color.ofOpaque(integer));
        return this;
    }
    
    public ColorFieldBuilder setSaveConsumer3(Consumer<TextColor> saveConsumer) {
        this.saveConsumer = integer -> saveConsumer.accept(TextColor.fromRgb(integer));
        return this;
    }
    
    public ColorFieldBuilder setDefaultValue(Supplier<Integer> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public ColorFieldBuilder setDefaultValue2(Supplier<Color> defaultValue) {
        this.defaultValue = () -> defaultValue.get().getColor();
        return this;
    }
    
    public ColorFieldBuilder setDefaultValue3(Supplier<TextColor> defaultValue) {
        this.defaultValue = () -> defaultValue.get().getValue();
        return this;
    }
    
    public ColorFieldBuilder setAlphaMode(boolean withAlpha) {
        this.alpha = withAlpha;
        return this;
    }
    
    public ColorFieldBuilder setDefaultValue(int defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public ColorFieldBuilder setDefaultValue(TextColor defaultValue) {
        this.defaultValue = () -> Objects.requireNonNull(defaultValue).getValue();
        return this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public ColorFieldBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public ColorFieldBuilder setTooltipSupplier(Function<Integer, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public ColorFieldBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    public ColorFieldBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public ColorEntry build() {
        ColorEntry entry = new ColorEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        if (this.alpha) {
            entry.withAlpha();
        } else {
            entry.withoutAlpha();
        }
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
