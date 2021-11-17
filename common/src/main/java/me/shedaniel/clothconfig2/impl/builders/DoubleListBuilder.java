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
public class DoubleListBuilder extends FieldBuilder<List<Double>, DoubleListListEntry> {
    
    protected Function<Double, Optional<Component>> cellErrorSupplier;
    private Consumer<List<Double>> saveConsumer = null;
    private Function<List<Double>, Optional<Component[]>> tooltipSupplier = list -> Optional.empty();
    private final List<Double> value;
    private boolean expanded = false;
    private Double min = null, max = null;
    private Function<DoubleListListEntry, DoubleListListEntry.DoubleListCell> createNewInstance;
    private Component addTooltip = new TranslatableComponent("text.cloth-config.list.add"), removeTooltip = new TranslatableComponent("text.cloth-config.list.remove");
    private boolean deleteButtonEnabled = true, insertInFront = false;
    
    public DoubleListBuilder(Component resetButtonKey, Component fieldNameKey, List<Double> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public Function<Double, Optional<Component>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public DoubleListBuilder setCellErrorSupplier(Function<Double, Optional<Component>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
        return this;
    }
    
    public DoubleListBuilder setErrorSupplier(Function<List<Double>, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public DoubleListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
        return this;
    }
    
    public DoubleListBuilder setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
        return this;
    }
    
    public DoubleListBuilder setAddButtonTooltip(Component addTooltip) {
        this.addTooltip = addTooltip;
        return this;
    }
    
    public DoubleListBuilder setRemoveButtonTooltip(Component removeTooltip) {
        this.removeTooltip = removeTooltip;
        return this;
    }
    
    public DoubleListBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public DoubleListBuilder setCreateNewInstance(Function<DoubleListListEntry, DoubleListListEntry.DoubleListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    public DoubleListBuilder setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }
    
    public DoubleListBuilder setSaveConsumer(Consumer<List<Double>> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public DoubleListBuilder setDefaultValue(Supplier<List<Double>> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public DoubleListBuilder setMin(double min) {
        this.min = min;
        return this;
    }
    
    public DoubleListBuilder setMax(double max) {
        this.max = max;
        return this;
    }
    
    public DoubleListBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public DoubleListBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    public DoubleListBuilder setDefaultValue(List<Double> defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public DoubleListBuilder setTooltipSupplier(Function<List<Double>, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public DoubleListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = list -> tooltipSupplier.get();
        return this;
    }
    
    public DoubleListBuilder setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = list -> tooltip;
        return this;
    }
    
    public DoubleListBuilder setTooltip(Component... tooltip) {
        this.tooltipSupplier = list -> Optional.ofNullable(tooltip);
        return this;
    }
    
    @NotNull
    @Override
    public DoubleListListEntry build() {
        DoubleListListEntry entry = new DoubleListListEntry(getFieldNameKey(), value, expanded, null, saveConsumer, defaultValue, getResetButtonKey(), requireRestart, deleteButtonEnabled, insertInFront);
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
