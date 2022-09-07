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

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractListBuilder<T, A extends AbstractConfigListEntry, SELF extends AbstractListBuilder<T, A, SELF>> extends AbstractFieldBuilder<List<T>, A, SELF> {
    protected Function<T, Optional<Component>> cellErrorSupplier;
    private boolean expanded = false;
    private Component addTooltip = Component.translatable("text.cloth-config.list.add");
    private Component removeTooltip = Component.translatable("text.cloth-config.list.remove");
    private boolean insertButtonEnabled = true, deleteButtonEnabled = true, insertInFront = false;
    
    protected AbstractListBuilder(Component resetButtonKey, Component fieldNameKey) {
        super(resetButtonKey, fieldNameKey);
    }
    
    public Function<T, Optional<Component>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public SELF setCellErrorSupplier(Function<T, Optional<Component>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
        return (SELF) this;
    }
    
    public SELF setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
        return (SELF) this;
    }

    public SELF setInsertButtonEnabled(boolean insertButtonEnabled) {
        this.insertButtonEnabled = insertButtonEnabled;
        return (SELF) this;
    }
    
    public SELF setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
        return (SELF) this;
    }
    
    public SELF setAddButtonTooltip(Component addTooltip) {
        this.addTooltip = addTooltip;
        return (SELF) this;
    }
    
    public SELF setRemoveButtonTooltip(Component removeTooltip) {
        this.removeTooltip = removeTooltip;
        return (SELF) this;
    }
    
    public SELF setExpanded(boolean expanded) {
        this.expanded = expanded;
        return (SELF) this;
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public Component getAddTooltip() {
        return addTooltip;
    }
    
    public Component getRemoveTooltip() {
        return removeTooltip;
    }
    
    public boolean isInsertButtonEnabled() {
        return insertButtonEnabled;
    }

    public boolean isDeleteButtonEnabled() {
        return deleteButtonEnabled;
    }
    
    public boolean isInsertInFront() {
        return insertInFront;
    }
}
