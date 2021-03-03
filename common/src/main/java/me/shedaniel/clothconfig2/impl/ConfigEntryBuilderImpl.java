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

package me.shedaniel.clothconfig2.impl;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ConfigEntryBuilderImpl implements ConfigEntryBuilder {
    
    private Component resetButtonKey = new TranslatableComponent("text.cloth-config.reset_value");
    
    private ConfigEntryBuilderImpl() {
    }
    
    public static ConfigEntryBuilderImpl create() {
        return new ConfigEntryBuilderImpl();
    }
    
    public static ConfigEntryBuilderImpl createImmutable() {
        return new ConfigEntryBuilderImpl() {
            @Override
            public ConfigEntryBuilder setResetButtonKey(Component resetButtonKey) {
                throw new UnsupportedOperationException("This is an immutable entry builder!");
            }
        };
    }
    
    @Override
    public Component getResetButtonKey() {
        return resetButtonKey;
    }
    
    @Override
    public ConfigEntryBuilder setResetButtonKey(Component resetButtonKey) {
        this.resetButtonKey = resetButtonKey;
        return this;
    }
    
    @Override
    public IntListBuilder startIntList(Component fieldNameKey, List<Integer> value) {
        return new IntListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public LongListBuilder startLongList(Component fieldNameKey, List<Long> value) {
        return new LongListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public FloatListBuilder startFloatList(Component fieldNameKey, List<Float> value) {
        return new FloatListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public DoubleListBuilder startDoubleList(Component fieldNameKey, List<Double> value) {
        return new DoubleListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public StringListBuilder startStrList(Component fieldNameKey, List<String> value) {
        return new StringListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public SubCategoryBuilder startSubCategory(Component fieldNameKey) {
        return new SubCategoryBuilder(resetButtonKey, fieldNameKey);
    }
    
    @Override
    public SubCategoryBuilder startSubCategory(Component fieldNameKey, List<AbstractConfigListEntry> entries) {
        SubCategoryBuilder builder = new SubCategoryBuilder(resetButtonKey, fieldNameKey);
        builder.addAll(entries);
        return builder;
    }
    
    @Override
    public BooleanToggleBuilder startBooleanToggle(Component fieldNameKey, boolean value) {
        return new BooleanToggleBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public StringFieldBuilder startStrField(Component fieldNameKey, String value) {
        return new StringFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public ColorFieldBuilder startColorField(Component fieldNameKey, int value) {
        return new ColorFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public TextFieldBuilder startTextField(Component fieldNameKey, String value) {
        return new TextFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public TextDescriptionBuilder startTextDescription(Component value) {
        return new TextDescriptionBuilder(resetButtonKey, new TextComponent(UUID.randomUUID().toString()), value);
    }
    
    @Override
    public <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(Component fieldNameKey, Class<T> clazz, T value) {
        return new EnumSelectorBuilder<>(resetButtonKey, fieldNameKey, clazz, value);
    }
    
    @Override
    public <T> SelectorBuilder<T> startSelector(Component fieldNameKey, T[] valuesArray, T value) {
        return new SelectorBuilder<>(resetButtonKey, fieldNameKey, valuesArray, value);
    }
    
    @Override
    public IntFieldBuilder startIntField(Component fieldNameKey, int value) {
        return new IntFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public LongFieldBuilder startLongField(Component fieldNameKey, long value) {
        return new LongFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public FloatFieldBuilder startFloatField(Component fieldNameKey, float value) {
        return new FloatFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public DoubleFieldBuilder startDoubleField(Component fieldNameKey, double value) {
        return new DoubleFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public IntSliderBuilder startIntSlider(Component fieldNameKey, int value, int min, int max) {
        return new IntSliderBuilder(resetButtonKey, fieldNameKey, value, min, max);
    }
    
    @Override
    public IntSliderListBuilder startIntSliderList(Component fieldNameKey, List<Integer> value, int min, int max) {
        return new IntSliderListBuilder(resetButtonKey, fieldNameKey, value, min, max);
    }

    @Override
    public LongSliderBuilder startLongSlider(Component fieldNameKey, long value, long min, long max) {
        return new LongSliderBuilder(resetButtonKey, fieldNameKey, value, min, max);
    }
    
    @Override
    public LongSliderListBuilder startLongSliderList(Component fieldNameKey, List<Long> value, long min, long max) {
        return new LongSliderListBuilder(resetButtonKey, fieldNameKey, value, min, max);
    }

    @Override
    public KeyCodeBuilder startModifierKeyCodeField(Component fieldNameKey, ModifierKeyCode value) {
        return new KeyCodeBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public <T> DropdownMenuBuilder<T> startDropdownMenu(Component fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator) {
        return new DropdownMenuBuilder<>(resetButtonKey, fieldNameKey, topCellElement, cellCreator);
    }
    
}
