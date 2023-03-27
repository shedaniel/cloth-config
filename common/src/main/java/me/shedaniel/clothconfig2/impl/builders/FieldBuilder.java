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
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
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
    @Nullable protected Dependency enableIfDependency = null;
    @Nullable protected Dependency showIfDependency = null;
    
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
     * @param field the config entry to finish building
     * @return the finished config entry
     */
    @Contract(value = "_ -> param1", mutates = "param1")
    protected A finishBuilding(A field) {
        if (field == null)
            return null;
        if (enableIfDependency != null)
            field.setEnableIfDependency(enableIfDependency);
        if (showIfDependency != null)
            field.setShowIfDependency(showIfDependency);
        return field;
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
     * Sets a dependency that when unmet will disable the built config entry.
     * <br><br>
     * If an "enable if" dependency is already set, it will be overwritten. If you wish for the config entry to have multiple
     * dependencies, you can pass this method a {@link me.shedaniel.clothconfig2.impl.dependencies.DependencyGroup DependencyGroup}
     * built using {@link Dependency#groupBuilder()} or one of the various helper methods such as {@link Dependency#all(Dependency...)}.
     *
     * @param dependency the {@link Dependency dependency} required to enable the config entry
     * @return this instance, for chaining
     * @see Dependency 
     */
    @Contract(mutates = "this")
    @SuppressWarnings("unchecked")
    public final SELF setEnabledIf(Dependency dependency) {
        this.enableIfDependency = dependency;
        return (SELF) this;
    }
    
    /**
     * Sets a dependency that when unmet will cause the built config entry to be hidden from menus.
     * <br><br>
     * If a "show if" dependency is already set, it will be overwritten. If you wish for the config entry to have multiple
     * dependencies, you can pass this method a {@link me.shedaniel.clothconfig2.impl.dependencies.DependencyGroup DependencyGroup}
     * built using {@link Dependency#groupBuilder()} or one of the various helper methods such as {@link Dependency#all(Dependency...)}.
     * 
     * @param dependency the {@link Dependency dependency} required to show the config entry in menus
     * @return this instance, for chaining
      @see Dependency 
     */
    @Contract(mutates = "this")
    @SuppressWarnings("unchecked")
    public final SELF setShownIf(Dependency dependency) {
        this.showIfDependency = dependency;
        return (SELF) this;
    }
}
