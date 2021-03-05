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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
public abstract class FieldBuilder<T, A extends AbstractConfigListEntry<?>, B extends FieldBuilder<T, A, B>> {
    @NotNull private final Component fieldNameKey;
    @NotNull private final Component resetButtonKey;
    protected boolean requireRestart = false;
    @Nullable protected Supplier<T> defaultValue = null;
    @Nullable protected Function<T, Optional<Component>> errorSupplier;
    @Nullable protected Consumer<T> saveConsumer = null;
    protected Function<T, Optional<Component[]>> tooltipSupplier = list -> Optional.empty();
    
    protected FieldBuilder(Component resetButtonKey, Component fieldNameKey) {
        this.resetButtonKey = Objects.requireNonNull(resetButtonKey);
        this.fieldNameKey = Objects.requireNonNull(fieldNameKey);
    }
    
    @Nullable
    public final Supplier<T> getDefaultValue() {
        return defaultValue;
    }
    
    @SuppressWarnings("rawtypes")
    @Deprecated
    public final AbstractConfigListEntry buildEntry() {
        return build();
    }
    
    @NotNull
    public abstract A build();
    
    @NotNull
    public final Component getFieldNameKey() {
        return fieldNameKey;
    }
    
    @NotNull
    public final Component getResetButtonKey() {
        return resetButtonKey;
    }
    
    public boolean isRequireRestart() {
        return requireRestart;
    }
    
    public B setErrorSupplier(@Nullable Function<T, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return (B) this;
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public void requireRestart(boolean requireRestart) {
        this.requireRestart = requireRestart;
    }
    
    public B requiresRestart(boolean requireRestart) {
        this.requireRestart = requireRestart;
        return (B) this;
    }
    
    public B requiresRestart() {
        this.requireRestart = true;
        return (B) this;
    }
    
    public B setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return (B) this;
    }
    
    public B setDefaultValue(Supplier<T> defaultValue) {
        this.defaultValue = defaultValue;
        return (B) this;
    }
    
    public B setDefaultValue(T defaultValue) {
        this.defaultValue = () -> defaultValue;
        return (B) this;
    }
    
    public B setTooltip(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = str -> tooltipSupplier.get();
        return (B) this;
    }
    
    public B setTooltip(Function<T, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return (B) this;
    }
    
    public B setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = str -> tooltip;
        return (B) this;
    }
    
    public B setTooltip(Component... tooltip) {
        this.tooltipSupplier = str -> Optional.ofNullable(tooltip);
        return (B) this;
    }
}
