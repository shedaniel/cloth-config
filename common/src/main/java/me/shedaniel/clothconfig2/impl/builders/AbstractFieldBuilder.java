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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractFieldBuilder<T, A extends AbstractConfigListEntry, SELF extends FieldBuilder<T, A, SELF>> extends FieldBuilder<T, A, SELF> {
    private Consumer<T> saveConsumer = null;
    private Function<T, Optional<Component[]>> tooltipSupplier = list -> Optional.empty();
    protected T value;
    
    protected AbstractFieldBuilder(Component resetButtonKey, Component fieldNameKey) {
        super(resetButtonKey, fieldNameKey);
    }
    
    public SELF requireRestart() {
        requireRestart(true);
        return (SELF) this;
    }
    
    public SELF setDefaultValue(Supplier<T> defaultValue) {
        this.defaultValue = defaultValue;
        return (SELF) this;
    }
    
    public SELF setDefaultValue(T defaultValue) {
        this.defaultValue = () -> defaultValue;
        return (SELF) this;
    }
    
    public SELF setErrorSupplier(Function<T, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return (SELF) this;
    }
    
    public SELF setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return (SELF) this;
    }
    
    public SELF setTooltipSupplier(Function<T, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return (SELF) this;
    }
    
    public SELF setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = list -> tooltipSupplier.get();
        return (SELF) this;
    }
    
    public SELF setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = list -> tooltip;
        return (SELF) this;
    }
    
    public SELF setTooltip(Component... tooltip) {
        this.tooltipSupplier = list -> Optional.ofNullable(tooltip);
        return (SELF) this;
    }
    
    public Consumer<T> getSaveConsumer() {
        return saveConsumer;
    }
    
    public Function<T, Optional<Component[]>> getTooltipSupplier() {
        return tooltipSupplier;
    }
}
