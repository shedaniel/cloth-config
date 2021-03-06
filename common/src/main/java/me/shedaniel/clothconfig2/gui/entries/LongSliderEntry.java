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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A config entry containing a single bounded {@link Long} with an
 * {@link net.minecraft.client.gui.components.AbstractSliderButton} for
 * user display and input.
 */
@Environment(EnvType.CLIENT)
public class LongSliderEntry extends AbstractSliderEntry<Long, LongSliderEntry> {
    protected AtomicLong value;

    @ApiStatus.Internal
    @Deprecated
    public LongSliderEntry(Component fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer, Component resetButtonKey, Supplier<Long> defaultValue) {
        this(fieldName, minimum, maximum, value, saveConsumer, resetButtonKey, defaultValue, null);
    }

    @ApiStatus.Internal
    @Deprecated
    public LongSliderEntry(Component fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer, Component resetButtonKey, Supplier<Long> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, minimum, maximum, value, saveConsumer, resetButtonKey, defaultValue, tooltipSupplier, false);
    }

    @ApiStatus.Internal
    @Deprecated
    public LongSliderEntry(Component fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer, Component resetButtonKey, Supplier<Long> defaultValue, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, minimum, maximum, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, requiresRestart);

        this.value = new AtomicLong(value);
        syncValueToSlider();
    }

    @Override
    protected LongSliderEntry self() {
        return this;
    }

    @Override
    public Long getValue() {
        return value.get();
    }

    @Override
    protected void setValue(Long value) {
        this.value.set(value);
    }

    @Override
    protected double getValueForSlider() {
        return ((double) this.value.get() - minimum) / Math.abs(maximum - minimum);
    }

    @Override
    protected void setValueFromSlider(double value) {
        this.value.set((long) (minimum + Math.abs(maximum - minimum) * value));
    }
}
