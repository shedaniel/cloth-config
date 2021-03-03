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

package me.shedaniel.clothconfig2.gui.entries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @param <T>    the configuration object type
 * @param <C>    the cell type
 * @param <SELF> the "curiously recurring template pattern" type parameter
 * @see BaseListEntry
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractListListEntry<T, C extends AbstractListListEntry.AbstractListCell<T, C, SELF>, SELF extends AbstractListListEntry<T, C, SELF>> extends BaseListEntry<T, C, SELF> {
    
    protected final BiFunction<T, SELF, C> createNewCell;
    protected Function<T, Optional<Component>> cellErrorSupplier;
    protected List<T> original;
    
    @ApiStatus.Internal
    public AbstractListListEntry(Component fieldName, List<T> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront, BiFunction<T, SELF, C> createNewCell) {
        super(fieldName, tooltipSupplier, defaultValue, abstractListListEntry -> createNewCell.apply(null, abstractListListEntry), saveConsumer, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront);
        this.createNewCell = createNewCell;
        this.original = new ArrayList<T>(value);
        for (T f : value)
            cells.add(createNewCell.apply(f, this.self()));
        this.widgets.addAll(cells);
        setExpanded(defaultExpanded);
    }
    
    public Function<T, Optional<Component>> getCellErrorSupplier() {
        return cellErrorSupplier;
    }
    
    public void setCellErrorSupplier(Function<T, Optional<Component>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
    }
    
    @Override
    public List<T> getValue() {
        return cells.stream().map(C::getValue).collect(Collectors.toList());
    }
    
    @Override
    protected C getFromValue(T value) {
        return createNewCell.apply(value, this.self());
    }
    
    @Override
    public boolean isEdited() {
        if (super.isEdited()) return true;
        List<T> value = getValue();
        if (value.size() != original.size()) return true;
        for (int i = 0; i < value.size(); i++) {
            if (!Objects.equals(value.get(i), original.get(i)))
                return true;
        }
        return false;
    }
    
    /**
     * @param <T>          the configuration object type
     * @param <SELF>       the "curiously recurring template pattern" type parameter for this class
     * @param <OUTER_SELF> the "curiously recurring template pattern" type parameter for the outer class
     * @see AbstractListListEntry
     */
    @ApiStatus.Internal
    public static abstract class AbstractListCell<T, SELF extends AbstractListCell<T, SELF, OUTER_SELF>, OUTER_SELF extends AbstractListListEntry<T, SELF, OUTER_SELF>> extends BaseListCell {
        protected final OUTER_SELF listListEntry;
        
        public AbstractListCell(@Nullable T value, OUTER_SELF listListEntry) {
            this.listListEntry = listListEntry;
            this.setErrorSupplier(() -> Optional.ofNullable(listListEntry.cellErrorSupplier).flatMap(cellErrorFn -> cellErrorFn.apply(this.getValue())));
        }
        
        public abstract T getValue();
    }
    
}
