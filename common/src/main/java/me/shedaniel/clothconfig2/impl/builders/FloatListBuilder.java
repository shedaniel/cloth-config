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

import me.shedaniel.clothconfig2.gui.entries.FloatListListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FloatListBuilder extends AbstractRangeListBuilder<Float, FloatListListEntry, FloatListBuilder> {
    private Function<FloatListListEntry, FloatListListEntry.FloatListCell> createNewInstance;
    
    public FloatListBuilder(Component resetButtonKey, Component fieldNameKey, List<Float> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public Function<Float, Optional<Component>> getCellErrorSupplier() {
        return super.getCellErrorSupplier();
    }
    
    @Override
    public FloatListBuilder setCellErrorSupplier(Function<Float, Optional<Component>> cellErrorSupplier) {
        return super.setCellErrorSupplier(cellErrorSupplier);
    }
    
    @Override
    public FloatListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        return super.setDeleteButtonEnabled(deleteButtonEnabled);
    }
    
    @Override
    public FloatListBuilder setErrorSupplier(Function<List<Float>, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public FloatListBuilder setInsertInFront(boolean insertInFront) {
        return super.setInsertInFront(insertInFront);
    }
    
    @Override
    public FloatListBuilder setAddButtonTooltip(Component addTooltip) {
        return super.setAddButtonTooltip(addTooltip);
    }
    
    @Override
    public FloatListBuilder setRemoveButtonTooltip(Component removeTooltip) {
        return super.setRemoveButtonTooltip(removeTooltip);
    }
    
    @Override
    public FloatListBuilder requireRestart() {
        return super.requireRestart();
    }
    
    public FloatListBuilder setCreateNewInstance(Function<FloatListListEntry, FloatListListEntry.FloatListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    @Override
    public FloatListBuilder setExpanded(boolean expanded) {
        return super.setExpanded(expanded);
    }
    
    @Override
    public FloatListBuilder setSaveConsumer(Consumer<List<Float>> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public FloatListBuilder setDefaultValue(Supplier<List<Float>> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public FloatListBuilder setMin(float min) {
        this.min = min;
        return this;
    }
    
    public FloatListBuilder setMax(float max) {
        this.max = max;
        return this;
    }
    
    @Override
    public FloatListBuilder removeMin() {
        return super.removeMin();
    }
    
    @Override
    public FloatListBuilder removeMax() {
        return super.removeMax();
    }
    
    @Override
    public FloatListBuilder setDefaultValue(List<Float> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public FloatListBuilder setTooltipSupplier(Function<List<Float>, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public FloatListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public FloatListBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public FloatListBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public FloatListListEntry build() {
        FloatListListEntry entry = new FloatListListEntry(getFieldNameKey(), value, isExpanded(), null, getSaveConsumer(), defaultValue, getResetButtonKey(), isRequireRestart(), isDeleteButtonEnabled(), isInsertInFront());
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
        
        return entry;
    }
    
}
