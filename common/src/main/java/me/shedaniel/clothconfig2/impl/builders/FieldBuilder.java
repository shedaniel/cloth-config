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
import me.shedaniel.clothconfig2.api.Requirement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class FieldBuilder<T, A extends AbstractConfigListEntry, SELF extends FieldBuilder<T, A, SELF>> {
    @NotNull private final Component fieldNameKey;
    @NotNull private final Component resetButtonKey;
    protected boolean requireRestart = false;
    @Nullable protected Supplier<T> defaultValue = null;
    @Nullable protected Function<T, Optional<Component>> errorSupplier;
    @Nullable protected Requirement enableRequirement = null;
    @Nullable protected Requirement displayRequirement = null;
    
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
    
    /**
     * Finishes building the given {@link AbstractConfigListEntry config entry} by applying anything defined in this abstract class.
     * <br><br>
     * Should be used by implementations of {@link #build()}.
     *
     * @param gui the config entry to finish building
     * @return the mutated config entry
     */
    @Contract(value = "_ -> param1", mutates = "param1")
    protected A finishBuilding(A gui) {
        if (gui == null)
            return null;
        if (enableRequirement != null)
            gui.setRequirement(enableRequirement);
        if (displayRequirement != null)
            gui.setDisplayRequirement(displayRequirement);
        return gui;
    }
    
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
    
    public void requireRestart(boolean requireRestart) {
        this.requireRestart = requireRestart;
    }
    
    /**
     * Set a requirement that controls whether the config entry gui is enabled.
     * 
     * <p>If an enablement requirement is already set, it will be overwritten.
     * 
     * <p>If the requirement returns {@code true}, the config entry will be enabled.
     *    If the requirement returns {@code false}, the config entry will be disabled.
     *
     * @see Requirement 
     */
    @Contract(mutates = "this")
    @ApiStatus.Experimental
    public final SELF setRequirement(Requirement requirement) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        enableRequirement = requirement;
        return self;
    }
    
    /**
     * Set a requirement that controls whether the config entry gui is displayed.
     *
     * <p>If a display requirement is already set, it will be overwritten.
     * 
     * <p>If the requirement returns {@code true}, the config entry will be displayed.
     *    If the requirement returns {@code false}, the config entry will be hidden.
     *
     * @see Requirement 
     */
    @Contract(mutates = "this")
    @ApiStatus.Experimental
    public final SELF setDisplayRequirement(Requirement requirement) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        displayRequirement = requirement;
        return self;
    }
}
