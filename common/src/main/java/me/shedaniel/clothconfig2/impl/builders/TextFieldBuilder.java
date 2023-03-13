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

import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TextFieldBuilder extends AbstractFieldBuilder<String, StringListEntry, TextFieldBuilder> {
    public TextFieldBuilder(Component resetButtonKey, Component fieldNameKey, String value) {
        super(resetButtonKey, fieldNameKey);
        Objects.requireNonNull(value);
        this.value = value;
    }
    
    @Override
    public TextFieldBuilder setErrorSupplier(Function<String, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public TextFieldBuilder requireRestart() {
        return super.requireRestart();
    }
    
    @Override
    public TextFieldBuilder setSaveConsumer(Consumer<String> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public TextFieldBuilder setDefaultValue(Supplier<String> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public TextFieldBuilder setDefaultValue(String defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public TextFieldBuilder setTooltipSupplier(Function<String, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public TextFieldBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public TextFieldBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public TextFieldBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public StringListEntry build() {
        StringListEntry entry = new StringListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, getSaveConsumer(), null, isRequireRestart());
        entry.setTooltipSupplier(() -> getTooltipSupplier().apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        if (dependency != null)
            entry.setDependency(dependency);
        return entry;
    }
    
}
