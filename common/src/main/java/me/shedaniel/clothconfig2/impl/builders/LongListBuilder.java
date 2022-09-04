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

import me.shedaniel.clothconfig2.gui.entries.LongListListEntry;
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
public class LongListBuilder extends AbstractRangeListBuilder<Long, LongListListEntry, LongListBuilder> {
    private Function<LongListListEntry, LongListListEntry.LongListCell> createNewInstance;
    
    public LongListBuilder(Component resetButtonKey, Component fieldNameKey, List<Long> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public Function<Long, Optional<Component>> getCellErrorSupplier() {
        return super.getCellErrorSupplier();
    }
    
    @Override
    public LongListBuilder setCellErrorSupplier(Function<Long, Optional<Component>> cellErrorSupplier) {
        return super.setCellErrorSupplier(cellErrorSupplier);
    }
    
    @Override
    public LongListBuilder setErrorSupplier(Function<List<Long>, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public LongListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        return super.setDeleteButtonEnabled(deleteButtonEnabled);
    }
    
    @Override
    public LongListBuilder setInsertInFront(boolean insertInFront) {
        return super.setInsertInFront(insertInFront);
    }
    
    @Override
    public LongListBuilder setAddButtonTooltip(Component addTooltip) {
        return super.setAddButtonTooltip(addTooltip);
    }
    
    @Override
    public LongListBuilder setRemoveButtonTooltip(Component removeTooltip) {
        return super.setRemoveButtonTooltip(removeTooltip);
    }
    
    @Override
    public LongListBuilder requireRestart() {
        return super.requireRestart();
    }
    
    public LongListBuilder setCreateNewInstance(Function<LongListListEntry, LongListListEntry.LongListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    @Override
    public LongListBuilder setExpanded(boolean expanded) {
        return super.setExpanded(expanded);
    }
    
    @Override
    public LongListBuilder setSaveConsumer(Consumer<List<Long>> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public LongListBuilder setDefaultValue(Supplier<List<Long>> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    public LongListBuilder setMin(long min) {
        this.min = min;
        return this;
    }
    
    public LongListBuilder setMax(long max) {
        this.max = max;
        return this;
    }
    
    @Override
    public LongListBuilder removeMin() {
        return super.removeMin();
    }
    
    @Override
    public LongListBuilder removeMax() {
        return super.removeMax();
    }
    
    @Override
    public LongListBuilder setDefaultValue(List<Long> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public LongListBuilder setTooltipSupplier(Function<List<Long>, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public LongListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public LongListBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public LongListBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public LongListListEntry build() {
        LongListListEntry entry = new LongListListEntry(getFieldNameKey(), value, isExpanded(), null, getSaveConsumer(), defaultValue, getResetButtonKey(), isRequireRestart(), isDeleteButtonEnabled(), isInsertInFront());
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        if (createNewInstance != null)
            entry.setCreateNewInstance(createNewInstance);
        entry.setCellErrorSupplier(cellErrorSupplier);
        entry.setTooltipSupplier(() -> getTooltipSupplier().apply(entry.getValue()));
        entry.setAddTooltip(getAddTooltip());
        entry.setRemoveTooltip(getRemoveTooltip());
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
