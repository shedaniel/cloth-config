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

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class KeyCodeBuilder extends FieldBuilder<ModifierKeyCode, KeyCodeEntry, KeyCodeBuilder> {
    
    @Nullable private Consumer<ModifierKeyCode> saveConsumer = null;
    @NotNull private Function<ModifierKeyCode, Optional<Component[]>> tooltipSupplier = bool -> Optional.empty();
    private final ModifierKeyCode value;
    private boolean allowKey = true, allowMouse = true, allowModifiers = true;
    
    public KeyCodeBuilder(Component resetButtonKey, Component fieldNameKey, ModifierKeyCode value) {
        super(resetButtonKey, fieldNameKey);
        this.value = ModifierKeyCode.copyOf(value);
    }
    
    public KeyCodeBuilder setAllowModifiers(boolean allowModifiers) {
        this.allowModifiers = allowModifiers;
        if (!allowModifiers)
            value.setModifier(Modifier.none());
        return this;
    }
    
    public KeyCodeBuilder setAllowKey(boolean allowKey) {
        if (!allowMouse && !allowKey)
            throw new IllegalArgumentException();
        this.allowKey = allowKey;
        return this;
    }
    
    public KeyCodeBuilder setAllowMouse(boolean allowMouse) {
        if (!allowKey && !allowMouse)
            throw new IllegalArgumentException();
        this.allowMouse = allowMouse;
        return this;
    }
    
    public KeyCodeBuilder setErrorSupplier(@Nullable Function<InputConstants.Key, Optional<Component>> errorSupplier) {
        return setModifierErrorSupplier(keyCode -> errorSupplier.apply(keyCode.getKeyCode()));
    }
    
    public KeyCodeBuilder setModifierErrorSupplier(@Nullable Function<ModifierKeyCode, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public KeyCodeBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public KeyCodeBuilder setSaveConsumer(Consumer<InputConstants.Key> saveConsumer) {
        return setModifierSaveConsumer(keyCode -> saveConsumer.accept(keyCode.getKeyCode()));
    }
    
    public KeyCodeBuilder setDefaultValue(Supplier<InputConstants.Key> defaultValue) {
        return setModifierDefaultValue(() -> ModifierKeyCode.of(defaultValue.get(), Modifier.none()));
    }
    
    public KeyCodeBuilder setModifierSaveConsumer(Consumer<ModifierKeyCode> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public KeyCodeBuilder setModifierDefaultValue(Supplier<ModifierKeyCode> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public KeyCodeBuilder setDefaultValue(InputConstants.Key defaultValue) {
        return setDefaultValue(ModifierKeyCode.of(defaultValue, Modifier.none()));
    }
    
    public KeyCodeBuilder setDefaultValue(ModifierKeyCode defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public KeyCodeBuilder setTooltipSupplier(@NotNull Function<InputConstants.Key, Optional<Component[]>> tooltipSupplier) {
        return setModifierTooltipSupplier(keyCode -> tooltipSupplier.apply(keyCode.getKeyCode()));
    }
    
    public KeyCodeBuilder setModifierTooltipSupplier(@NotNull Function<ModifierKeyCode, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public KeyCodeBuilder setTooltipSupplier(@NotNull Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = bool -> tooltipSupplier.get();
        return this;
    }
    
    public KeyCodeBuilder setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = bool -> tooltip;
        return this;
    }
    
    public KeyCodeBuilder setTooltip(@Nullable Component... tooltip) {
        this.tooltipSupplier = bool -> Optional.ofNullable(tooltip);
        return this;
    }
    
    @NotNull
    @Override
    public KeyCodeEntry build() {
        KeyCodeEntry entry = new KeyCodeEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        entry.setAllowKey(allowKey);
        entry.setAllowMouse(allowMouse);
        entry.setAllowModifiers(allowModifiers);
        return entry;
    }
    
}
