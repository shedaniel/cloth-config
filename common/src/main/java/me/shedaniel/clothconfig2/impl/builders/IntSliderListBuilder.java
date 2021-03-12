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

import me.shedaniel.clothconfig2.gui.entries.IntegerSliderListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class IntSliderListBuilder extends AbstractSliderListBuilder<Integer, IntegerSliderListEntry.IntegerSliderListCell, IntegerSliderListEntry, IntSliderListBuilder> {
    public IntSliderListBuilder(Component resetButtonKey, Component fieldNameKey, List<Integer> value, int min, int max) {
        super(resetButtonKey, fieldNameKey, value, min, max);
    }

    @Override
    protected IntegerSliderListEntry buildEntry(Component fieldNameKey, Integer min, Integer max, List<Integer> value, boolean expanded, Supplier<Optional<Component[]>> tooltipSupplier, Consumer<List<Integer>> saveConsumer, Supplier<List<Integer>> defaultValue, Integer cellDefaultValue, Component resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        return new IntegerSliderListEntry(fieldNameKey, min, max, value, expanded, tooltipSupplier, saveConsumer, defaultValue, cellDefaultValue, resetButtonKey, requiresRestart, deleteButtonEnabled, insertInFront);
    }
}
