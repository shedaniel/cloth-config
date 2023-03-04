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

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
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
public class SelectorBuilder<T> extends AbstractFieldBuilder<T, SelectionListEntry<T>, SelectorBuilder<T>> {
    private final T[] valuesArray;
    private Function<T, Component> nameProvider = null;
    
    public SelectorBuilder(Component resetButtonKey, Component fieldNameKey, T[] valuesArray, T value) {
        super(resetButtonKey, fieldNameKey);
        Objects.requireNonNull(value);
        this.valuesArray = valuesArray;
        this.value = value;
    }
    
    @Override
    public SelectorBuilder<T> setErrorSupplier(Function<T, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public SelectorBuilder<T> requireRestart() {
        return super.requireRestart();
    }
    
    @Override
    public SelectorBuilder<T> setSaveConsumer(Consumer<T> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public SelectorBuilder<T> setDefaultValue(Supplier<T> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public SelectorBuilder<T> setDefaultValue(T defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public SelectorBuilder<T> setTooltipSupplier(Function<T, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public SelectorBuilder<T> setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public SelectorBuilder<T> setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public SelectorBuilder<T> setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    public SelectorBuilder<T> setNameProvider(Function<T, Component> enumNameProvider) {
        this.nameProvider = enumNameProvider;
        return this;
    }
    
    @NotNull
    @Override
    public SelectionListEntry<T> build() {
        SelectionListEntry<T> entry = new SelectionListEntry<>(getFieldNameKey(), valuesArray, value, getResetButtonKey(), defaultValue, getSaveConsumer(), nameProvider, null, isRequireRestart());
        entry.setTooltipSupplier(() -> getTooltipSupplier().apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        if (dependency != null) {
            entry.setDependency(dependency, dependantValue);
            entry.shouldHideWhenDisabled(hiddenWhenDisabled);
        }
        return entry;
    }
    
}
