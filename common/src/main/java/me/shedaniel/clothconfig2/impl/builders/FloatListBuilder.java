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
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FloatListBuilder extends FieldBuilder<List<Float>, FloatListListEntry, FloatListBuilder> {
    
    protected Function<Float, Optional<Component>> cellErrorSupplier;
    private final List<Float> value;
    private boolean expanded = false;
    private Float min = null, max = null;
    private Function<FloatListListEntry, FloatListListEntry.FloatListCell> createNewInstance;
    private Component addTooltip = new TranslatableComponent("text.cloth-config.list.add"), removeTooltip = new TranslatableComponent("text.cloth-config.list.remove");
    private boolean deleteButtonEnabled = true, insertInFront = true;
    
    public FloatListBuilder(Component resetButtonKey, Component fieldNameKey, List<Float> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public Function<Float, Optional<Component>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public FloatListBuilder setCellErrorSupplier(Function<Float, Optional<Component>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
        return this;
    }
    
    public FloatListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
        return this;
    }
    
    public FloatListBuilder setErrorSupplier(Function<List<Float>, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    public FloatListBuilder setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
        return this;
    }
    
    public FloatListBuilder setAddButtonTooltip(Component addTooltip) {
        this.addTooltip = addTooltip;
        return this;
    }
    
    public FloatListBuilder setRemoveButtonTooltip(Component removeTooltip) {
        this.removeTooltip = removeTooltip;
        return this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public FloatListBuilder requireRestart() {
        return super.requiresRestart();
    }
    
    public FloatListBuilder setCreateNewInstance(Function<FloatListListEntry, FloatListListEntry.FloatListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    public FloatListBuilder setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public FloatListBuilder setExpended(boolean expanded) {
        return setExpanded(expanded);
    }
    
    public FloatListBuilder setSaveConsumer(Consumer<List<Float>> saveConsumer) {
       return super.setSaveConsumer(saveConsumer);
    }
    
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
    
    public FloatListBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public FloatListBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    public FloatListBuilder setDefaultValue(List<Float> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public FloatListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public FloatListBuilder setTooltipSupplier(Function<List<Float>, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltip(tooltipSupplier);
    }
    
    public FloatListBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    public FloatListBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public FloatListListEntry build() {
        FloatListListEntry entry = new FloatListListEntry(getFieldNameKey(), value, expanded, null, saveConsumer, defaultValue, getResetButtonKey(), isRequireRestart(), deleteButtonEnabled, insertInFront);
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
