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
public class IntListBuilder extends FieldBuilder<List<Integer>, IntegerListListEntry, IntListBuilder> {
    
    protected Function<Integer, Optional<Component>> cellErrorSupplier;
    private final List<Integer> value;
    private boolean expanded = false;
    private Integer min = null, max = null;
    private Function<IntegerListListEntry, IntegerListListEntry.IntegerListCell> createNewInstance;
    private Component addTooltip = new TranslatableComponent("text.cloth-config.list.add"), removeTooltip = new TranslatableComponent("text.cloth-config.list.remove");
    private boolean deleteButtonEnabled = true, insertInFront = true;
    
    public IntListBuilder(Component resetButtonKey, Component fieldNameKey, List<Integer> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public Function<Integer, Optional<Component>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public IntListBuilder setCellErrorSupplier(Function<Integer, Optional<Component>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
        return this;
    }
    
    public IntListBuilder setErrorSupplier(Function<List<Integer>, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    public IntListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
        return this;
    }
    
    public IntListBuilder setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
        return this;
    }
    
    public IntListBuilder setAddButtonTooltip(Component addTooltip) {
        this.addTooltip = addTooltip;
        return this;
    }
    
    public IntListBuilder setRemoveButtonTooltip(Component removeTooltip) {
        this.removeTooltip = removeTooltip;
        return this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public IntListBuilder requireRestart() {
        return requiresRestart();
    }
    
    public IntListBuilder setCreateNewInstance(Function<IntegerListListEntry, IntegerListListEntry.IntegerListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    public IntListBuilder setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public IntListBuilder setExpended(boolean expanded) {
        return setExpanded(expanded);
    }
    
    public IntListBuilder setSaveConsumer(Consumer<List<Integer>> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
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
    
    public IntListBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public IntListBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    public IntListBuilder setDefaultValue(List<Integer> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public IntListBuilder setTooltipSupplier(Function<List<Integer>, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public IntListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public IntListBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    public IntListBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public IntegerListListEntry build() {
        IntegerListListEntry entry = new IntegerListListEntry(getFieldNameKey(), value, expanded, null, saveConsumer, defaultValue, getResetButtonKey(), isRequireRestart(), deleteButtonEnabled, insertInFront);
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        if (createNewInstance != null)
            entry.setCreateNewInstance(createNewInstance);
        entry.setCellErrorSupplier(cellErrorSupplier);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        entry.setAddTooltip(addTooltip);
        entry.setRemoveTooltip(removeTooltip);
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
