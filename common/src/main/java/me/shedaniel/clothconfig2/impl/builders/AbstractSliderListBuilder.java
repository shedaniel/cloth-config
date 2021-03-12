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

import me.shedaniel.clothconfig2.gui.entries.AbstractSliderListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class AbstractSliderListBuilder<T, C extends AbstractSliderListEntry.AbstractSliderListCell<T, C, E>, E extends AbstractSliderListEntry<T, C, E>, SELF extends AbstractSliderListBuilder<T, C, E, SELF>> extends FieldBuilder<List<T>, E> {
    private final List<T> value;
    private T cellDefaultValue = null;
    private T max;
    private T min;
    private boolean expanded = false;
    private Function<T, Component> textGetter = null;
    private Consumer<List<T>> saveConsumer = null;
    private boolean deleteButtonEnabled = true, insertInFront = true;
    private Function<E, C> createNewInstance;
    private Function<T, Optional<Component>> cellErrorSupplier;
    private Function<List<T>, Optional<Component[]>> tooltipSupplier = list -> Optional.empty();
    private Component addTooltip = new TranslatableComponent("text.cloth-config.list.add"), removeTooltip = new TranslatableComponent("text.cloth-config.list.remove");

    public AbstractSliderListBuilder(Component resetButtonKey, Component fieldNameKey, List<T> value, T min, T max) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setDefaultValue(Supplier<List<T>> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setDefaultValue(List<T> defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setCellDefaultValue(T cellDefaultValue) {
        this.cellDefaultValue = cellDefaultValue;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> removeCellDefaultValue() {
        this.cellDefaultValue = null;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setMax(T max) {
        this.max = max;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setMin(T min) {
        this.min = min;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setTextGetter(Function<T, Component> textGetter) {
        this.textGetter = textGetter;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setSaveConsumer(Consumer<List<T>> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setDeleteButtonEnabled(boolean deleteButtonEnabled) {
        this.deleteButtonEnabled = deleteButtonEnabled;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setInsertInFront(boolean insertInFront) {
        this.insertInFront = insertInFront;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setCreateNewInstance(Function<E, C> createNewInstance) {
        this.createNewInstance = createNewInstance;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setCellErrorSupplier(Function<T, Optional<Component>> cellErrorSupplier) {
        this.cellErrorSupplier = cellErrorSupplier;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setErrorSupplier(Function<List<T>, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = list -> tooltipSupplier.get();
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setTooltipSupplier(Function<List<T>, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = list -> tooltip;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setTooltip(Component... tooltip) {
        this.tooltipSupplier = list -> Optional.ofNullable(tooltip);
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setAddButtonTooltip(Component addTooltip) {
        this.addTooltip = addTooltip;
        return this;
    }

    public AbstractSliderListBuilder<T, C, E, SELF> setRemoveButtonTooltip(Component removeTooltip) {
        this.removeTooltip = removeTooltip;
        return this;
    }

    protected abstract E buildEntry(Component fieldNameKey, T min, T max, List<T> value, boolean expanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<T>> saveConsumer, Supplier<List<T>> defaultValue, T cellDefaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront);

    @Override
    public E build() {
        E entry = buildEntry(getFieldNameKey(), min, max, value, expanded, null, saveConsumer, defaultValue, cellDefaultValue == null ? min : cellDefaultValue, getResetButtonKey(), isRequireRestart(), deleteButtonEnabled, insertInFront);
        if (textGetter != null)
            entry.setTextGetter(textGetter);
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
