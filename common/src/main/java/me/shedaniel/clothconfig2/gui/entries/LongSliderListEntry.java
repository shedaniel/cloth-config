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
import net.minecraft.network.chat.TextComponent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A config entry list consisting of bounded {@link Long} values that use one
 * {@link LongSliderListCell} per entry.
 */
@Environment(EnvType.CLIENT)
public class LongSliderListEntry extends AbstractSliderListEntry<Long, LongSliderListEntry.LongSliderListCell, LongSliderListEntry> {
    private static final Function<Long, Component> DEFAULT_TEXT_GETTER = value -> new TextComponent(String.format("Value: %d", value));

    public LongSliderListEntry(Component fieldName, long minimum, long maximum, List<Long> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Long>> saveConsumer, Supplier<List<Long>> defaultValue, long cellDefaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        super(fieldName, minimum, maximum, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, cellDefaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, LongSliderListCell::new);
        setTextGetter(DEFAULT_TEXT_GETTER);
        cells.forEach(LongSliderListCell::syncValueToSlider);
    }

    @Override
    public LongSliderListEntry self() {
        return this;
    }

    /**
     * A config entry within a parent {@link LongSliderListEntry} containing a
     * single bounded {@link Long} with an
     * {@link net.minecraft.client.gui.components.AbstractSliderButton} for
     * user display and input.
     */
    public static class LongSliderListCell extends AbstractSliderListEntry.AbstractSliderListCell<Long, LongSliderListCell, LongSliderListEntry> {
        private final AtomicLong value;

        public LongSliderListCell(Long value, LongSliderListEntry listListEntry) {
            super(value, listListEntry);

            this.value = new AtomicLong(value == null ? listListEntry.cellDefaultValue : value);
        }

        @Override
        public Long getValue() {
            return value.get();
        }

        @Override
        protected double getValueForSlider() {
            return ((double) this.value.get() - listListEntry.minimum) / Math.abs(listListEntry.maximum - listListEntry.minimum);
        }

        @Override
        protected void setValueFromSlider(double value) {
            this.value.set((long) (listListEntry.minimum + Math.abs(listListEntry.maximum - listListEntry.minimum) * value));
        }
    }
}
