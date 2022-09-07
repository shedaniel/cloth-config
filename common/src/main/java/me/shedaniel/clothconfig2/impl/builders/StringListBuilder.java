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

import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
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
public class StringListBuilder extends AbstractListBuilder<String, StringListListEntry, StringListBuilder> {
    private Function<StringListListEntry, StringListListEntry.StringListCell> createNewInstance;
    
    public StringListBuilder(Component resetButtonKey, Component fieldNameKey, List<String> value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public Function<String, Optional<Component>> getCellErrorSupplier() {
        return super.getCellErrorSupplier();
    }
    
    @Override
    public StringListBuilder setCellErrorSupplier(Function<String, Optional<Component>> cellErrorSupplier) {
        return super.setCellErrorSupplier(cellErrorSupplier);
    }
    
    @Override
    public StringListBuilder setErrorSupplier(Function<List<String>, Optional<Component>> errorSupplier) {
        return super.setErrorSupplier(errorSupplier);
    }
    
    @Override
    public StringListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        return super.setDeleteButtonEnabled(deleteButtonEnabled);
    }
    
    @Override
    public StringListBuilder setInsertInFront(boolean insertInFront) {
        return super.setInsertInFront(insertInFront);
    }
    
    @Override
    public StringListBuilder setAddButtonTooltip(Component addTooltip) {
        return super.setAddButtonTooltip(addTooltip);
    }
    
    @Override
    public StringListBuilder setRemoveButtonTooltip(Component removeTooltip) {
        return super.setRemoveButtonTooltip(removeTooltip);
    }
    
    @Override
    public StringListBuilder requireRestart() {
        return super.requireRestart();
    }
    
    public StringListBuilder setCreateNewInstance(Function<StringListListEntry, StringListListEntry.StringListCell> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }
    
    @Override
    public StringListBuilder setExpanded(boolean expanded) {
        return super.setExpanded(expanded);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public StringListBuilder setExpended(boolean expanded) {
        return setExpanded(expanded);
    }
    
    @Override
    public StringListBuilder setSaveConsumer(Consumer<List<String>> saveConsumer) {
        return super.setSaveConsumer(saveConsumer);
    }
    
    @Override
    public StringListBuilder setDefaultValue(Supplier<List<String>> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public StringListBuilder setDefaultValue(List<String> defaultValue) {
        return super.setDefaultValue(defaultValue);
    }
    
    @Override
    public StringListBuilder setTooltipSupplier(Function<List<String>, Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public StringListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        return super.setTooltipSupplier(tooltipSupplier);
    }
    
    @Override
    public StringListBuilder setTooltip(Optional<Component[]> tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @Override
    public StringListBuilder setTooltip(Component... tooltip) {
        return super.setTooltip(tooltip);
    }
    
    @NotNull
    @Override
    public StringListListEntry build() {
        StringListListEntry entry = new StringListListEntry(getFieldNameKey(), value, isExpanded(), null, getSaveConsumer(), defaultValue, getResetButtonKey(), isRequireRestart(), isDeleteButtonEnabled(), isInsertInFront());
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
