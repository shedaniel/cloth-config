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

import me.shedaniel.clothconfig2.gui.entries.IntegerListListEntry;
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
public class IntListBuilder extends AbstractRangeListBuilder<Integer, IntegerListListEntry, IntListBuilder> {
    private Function<IntegerListListEntry, IntegerListListEntry.IntegerListCell> createNewInstance;
    
    
    public IntListBuilder(Component resetButtonKey, Component fieldNameKey, List<Integer> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public Function<Integer, Optional<Component>> getCellErrorSupplier() {
        return super.getCellErrorSupplier();
    }
    
    @Override
    public IntListBuilder setCellErrorSupplier(Function<Integer, Optional<Component>> cellErrorSupplier) {
        return super.setCellErrorSupplier(cellErrorSupplier);
    }
    
    @Override
    public IntListBuilder setErrorSupplier(Function<List<Integer>, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public IntListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        return super.setDeleteButtonEnabled(deleteButtonEnabled);
    }
    
    @Override
    public IntListBuilder setInsertInFront(boolean insertInFront) {
        return super.setInsertInFront(insertInFront);
    }
    
    @Override
    public IntListBuilder setAddButtonTooltip(Component addTooltip) {
        return super.setAddButtonTooltip(addTooltip);
    }
    
    @Override
    public IntListBuilder setRemoveButtonTooltip(Component removeTooltip) {
        return super.setRemoveButtonTooltip(removeTooltip);
    }
    
    @Override
    public IntListBuilder requireRestart() {
        return super.requireRestart();
    }
    
    public IntListBuilder setCreateNewInstance(Function<IntegerListListEntry, IntegerListListEntry.IntegerListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    @Override
    public IntListBuilder setExpanded(boolean expanded) {
        return super.setExpanded(expanded);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public IntListBuilder setExpended(boolean expanded) {
        return setExpanded(expanded);
    }
    
    @Override
    public IntListBuilder setSaveConsumer(Consumer<List<Integer>> saveConsumer) {
        return (IntListBuilder) super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public IntListBuilder setDefaultValue(Supplier<List<Integer>> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public IntListBuilder setMin(int min) {
        this.min = min;
        return this;
    }
    
    public IntListBuilder setMax(int max) {
        this.max = max;
        return this;
    }
    
    @Override
    public IntListBuilder removeMin() {
        return super.removeMin();
    }
    
    @Override
    public IntListBuilder removeMax() {
        return super.removeMax();
    }
    
    @Override
    public IntListBuilder setDefaultValue(List<Integer> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public IntListBuilder setTooltipSupplier(Function<List<Integer>, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public IntListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public IntListBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public IntListBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public IntegerListListEntry build() {
        IntegerListListEntry entry = new IntegerListListEntry(getFieldNameKey(), value, isExpanded(), null, getSaveConsumer(), defaultValue, getResetButtonKey(), isRequireRestart(), isDeleteButtonEnabled(), isInsertInFront());
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
