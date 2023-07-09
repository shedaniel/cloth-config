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

import me.shedaniel.clothconfig2.gui.entries.DoubleListListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleListBuilder extends AbstractRangeListBuilder<Double, DoubleListListEntry, DoubleListBuilder> {
    private Function<DoubleListListEntry, DoubleListListEntry.DoubleListCell> createNewInstance;
    
    public DoubleListBuilder(Component resetButtonKey, Component fieldNameKey, List<Double> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public Function<Double, Optional<Component>> getCellErrorSupplier() {
        return super.getCellErrorSupplier();
    }
    
    @Override
    public DoubleListBuilder setCellErrorSupplier(Function<Double, Optional<Component>> cellErrorSupplier) {
        return super.setCellErrorSupplier(cellErrorSupplier);
    }
    
    public DoubleListBuilder setErrorSupplier(Function<List<Double>, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    @Override
    public DoubleListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        return super.setDeleteButtonEnabled(deleteButtonEnabled);
    }
    
    @Override
    public DoubleListBuilder setInsertInFront(boolean insertInFront) {
        return super.setInsertInFront(insertInFront);
    }
    
    @Override
    public DoubleListBuilder setAddButtonTooltip(Component addTooltip) {
        return super.setAddButtonTooltip(addTooltip);
    }
    
    @Override
    public DoubleListBuilder setRemoveButtonTooltip(Component removeTooltip) {
        return super.setRemoveButtonTooltip(removeTooltip);
    }
    
    @Override
    public DoubleListBuilder requireRestart() {
        return super.requireRestart();
    }
    
    public DoubleListBuilder setCreateNewInstance(Function<DoubleListListEntry, DoubleListListEntry.DoubleListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    @Override
    public DoubleListBuilder setExpanded(boolean expanded) {
        return super.setExpanded(expanded);
    }
    
    @Override
    public DoubleListBuilder setSaveConsumer(Consumer<List<Double>> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public DoubleListBuilder setDefaultValue(Supplier<List<Double>> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public DoubleListBuilder setMin(double min) {
        this.min = min;
        return this;
    }
    
    public DoubleListBuilder setMax(double max) {
        this.max = max;
        return this;
    }
    
    @Override
    public DoubleListBuilder removeMin() {
        return super.removeMin();
    }
    
    @Override
    public DoubleListBuilder removeMax() {
        return super.removeMax();
    }
    
    public DoubleListBuilder setDefaultValue(List<Double> defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    @Override
    public DoubleListBuilder setTooltipSupplier(Function<List<Double>, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public DoubleListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public DoubleListBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public DoubleListBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public DoubleListListEntry build() {
        DoubleListListEntry entry = new DoubleListListEntry(getFieldNameKey(), value, isExpanded(), null, getSaveConsumer(), defaultValue, getResetButtonKey(), requireRestart, isDeleteButtonEnabled(), isInsertInFront());
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        if (createNewInstance != null)
            entry.setCreateNewInstance(createNewInstance);
        entry.setInsertButtonEnabled(isInsertButtonEnabled());
        entry.setCellErrorSupplier(cellErrorSupplier);
        entry.setTooltipSupplier(() -> getTooltipSupplier().apply(entry.getValue()));
        entry.setAddTooltip(getAddTooltip());
        entry.setRemoveTooltip(getRemoveTooltip());
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return finishBuilding(entry);
    }
    
}
