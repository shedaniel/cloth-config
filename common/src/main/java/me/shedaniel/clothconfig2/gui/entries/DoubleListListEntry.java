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

import me.shedaniel.clothconfig2.api.NumberListConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleListListEntry extends AbstractTextFieldListListEntry<Double, DoubleListListEntry.DoubleListCell, DoubleListListEntry> implements NumberListConfigEntry<Double> {
    
    private double minimum, maximum;
    
    @ApiStatus.Internal
    @Deprecated
    public DoubleListListEntry(Component fieldName, List<Double> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Double>> saveConsumer, Supplier<List<Double>> defaultValue, Component resetButtonKey) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public DoubleListListEntry(Component fieldName, List<Double> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Double>> saveConsumer, Supplier<List<Double>> defaultValue, Component resetButtonKey, boolean requiresRestart) {
        this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public DoubleListListEntry(Component fieldName, List<Double> value, boolean defaultExpanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Double>> saveConsumer, Supplier<List<Double>> defaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        super(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront, DoubleListCell::new);
        this.minimum = Double.NEGATIVE_INFINITY;
        this.maximum = Double.POSITIVE_INFINITY;
    }
    
    public DoubleListListEntry setMaximum(Double maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public DoubleListListEntry setMinimum(Double minimum) {
        this.minimum = minimum;
        return this;
    }
    
    @Override
    public DoubleListListEntry self() {
        return this;
    }
    
    public static class DoubleListCell extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Double, DoubleListCell, DoubleListListEntry> {
        
        public DoubleListCell(Double value, final DoubleListListEntry listListEntry) {
            super(value, listListEntry);
        }
        
        @Nullable
        @Override
        protected Double substituteDefault(@Nullable Double value) {
            if (value == null)
                return 0d;
            else
                return value;
        }
        
        @Override
        protected boolean isValidText(@NotNull String text) {
            return text.chars().allMatch(c -> Character.isDigit(c) || c == '-' || c == '.');
        }
        
        public Double getValue() {
            try {
                return Double.valueOf(widget.getValue());
            } catch (NumberFormatException e) {
                return 0d;
            }
        }
        
        @Override
        public Optional<Component> getError() {
            try {
                double i = Double.parseDouble(widget.getValue());
                if (i > listListEntry.maximum)
                    return Optional.of(Component.translatable("text.cloth-config.error.too_large", listListEntry.maximum));
                else if (i < listListEntry.minimum)
                    return Optional.of(Component.translatable("text.cloth-config.error.too_small", listListEntry.minimum));
            } catch (NumberFormatException ex) {
                return Optional.of(Component.translatable("text.cloth-config.error.not_valid_number_double"));
            }
            return Optional.empty();
        }
        
    }
    
}
