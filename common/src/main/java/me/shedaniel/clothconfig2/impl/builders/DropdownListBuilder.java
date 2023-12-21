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

import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DropdownListBuilder<T> extends FieldBuilder<List<T>, NestedListListEntry<T, DropdownBoxEntry<T>>, DropdownListBuilder<T>> {
	protected List<T> value;
	protected Supplier<T> defaultEntryValue = null;
    protected Function<T, SelectionTopCellElement<T>> topCellCreator;
    protected SelectionCellCreator<T> cellCreator;
    protected Supplier<Optional<Component[]>> tooltipSupplier = () -> Optional.empty();
    protected Consumer<List<T>> saveConsumer = null;
    protected Iterable<T> selections = Collections.emptyList();
    protected boolean suggestionMode = true;
    
    public DropdownListBuilder(Component resetButtonKey, Component fieldNameKey, List<T> value, Function<T, SelectionTopCellElement<T>> topCellCreator, SelectionCellCreator<T> cellCreator) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.topCellCreator = Objects.requireNonNull(topCellCreator);
        this.cellCreator = Objects.requireNonNull(cellCreator);
    }
    
    public DropdownListBuilder<T> setSelections(Iterable<T> selections) {
        this.selections = selections;
        return this;
    }
    
    public DropdownListBuilder<T> setDefaultValue(Supplier<List<T>> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public DropdownListBuilder<T> setDefaultValue(List<T> defaultValue) {
        this.defaultValue = () -> Objects.requireNonNull(defaultValue);
        return this;
    }
    
    public DropdownListBuilder<T> setDefaultEntryValue(Supplier<T> defaultEntryValue) {
        this.defaultEntryValue = defaultEntryValue;
        return this;
    }
    
    public DropdownListBuilder<T> setDefaultEntryValue(T defaultEntryValue) {
        this.defaultEntryValue = () -> Objects.requireNonNull(defaultEntryValue);
        return this;
    }
    
    public DropdownListBuilder<T> setSaveConsumer(Consumer<List<T>> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public DropdownListBuilder<T> setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public DropdownListBuilder<T> setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = () -> tooltip;
        return this;
    }
    
    public DropdownListBuilder<T> setTooltip(Component... tooltip) {
        this.tooltipSupplier = () -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public DropdownListBuilder<T> requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public DropdownListBuilder<T> setErrorSupplier(Function<List<T>, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public DropdownListBuilder<T> setSuggestionMode(boolean suggestionMode) {
        this.suggestionMode = suggestionMode;
        return this;
    }
    
    public boolean isSuggestionMode() {
        return suggestionMode;
    }
    
    @NotNull
    @Override
    public NestedListListEntry<T, DropdownBoxEntry<T>> build() {
    	NestedListListEntry<T, DropdownBoxEntry<T>> listEntry = new NestedListListEntry<T, DropdownBoxEntry<T>>(
    			getFieldNameKey(),
    			value,
				false,
				tooltipSupplier,
				saveConsumer,
				defaultValue,
				getResetButtonKey(),
				true,
				false,
				(entryValue, list) -> {
					Supplier<T> defaultValue = () -> entryValue;
					if(entryValue == null) defaultValue = defaultEntryValue;
					DropdownBoxEntry<T> entry = new DropdownBoxEntry<T>(CommonComponents.EMPTY, getResetButtonKey(), null, isRequireRestart(), defaultValue, null, selections, topCellCreator.apply(entryValue), cellCreator);
			        entry.setSuggestionMode(suggestionMode);
			        return entry;
				});
        if (errorSupplier != null)
        	listEntry.setErrorSupplier(() -> errorSupplier.apply(listEntry.getValue()));
    	
        
        return finishBuilding(listEntry);
    }
}
