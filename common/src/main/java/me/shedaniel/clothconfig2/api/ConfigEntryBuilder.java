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

package me.shedaniel.clothconfig2.api;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import me.shedaniel.clothconfig2.impl.builders.*;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder.TopCellElementBuilder;
import me.shedaniel.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public interface ConfigEntryBuilder {
    
    static ConfigEntryBuilder create() {
        return ConfigEntryBuilderImpl.create();
    }
    
    Component getResetButtonKey();
    
    ConfigEntryBuilder setResetButtonKey(Component resetButtonKey);
    
    IntListBuilder startIntList(Component fieldNameKey, List<Integer> value);
    
    LongListBuilder startLongList(Component fieldNameKey, List<Long> value);
    
    FloatListBuilder startFloatList(Component fieldNameKey, List<Float> value);
    
    DoubleListBuilder startDoubleList(Component fieldNameKey, List<Double> value);
    
    StringListBuilder startStrList(Component fieldNameKey, List<String> value);
    
    SubCategoryBuilder startSubCategory(Component fieldNameKey);
    
    SubCategoryBuilder startSubCategory(Component fieldNameKey, List<AbstractConfigListEntry> entries);
    
    BooleanToggleBuilder startBooleanToggle(Component fieldNameKey, boolean value);
    
    StringFieldBuilder startStrField(Component fieldNameKey, String value);
    
    ColorFieldBuilder startColorField(Component fieldNameKey, int value);
    
    default ColorFieldBuilder startColorField(Component fieldNameKey, TextColor color) {
        return startColorField(fieldNameKey, color.getValue());
    }
    
    default ColorFieldBuilder startColorField(Component fieldNameKey, Color color) {
        return startColorField(fieldNameKey, color.getColor() & 0xFFFFFF);
    }
    
    default ColorFieldBuilder startAlphaColorField(Component fieldNameKey, int value) {
        return startColorField(fieldNameKey, value).setAlphaMode(true);
    }
    
    default ColorFieldBuilder startAlphaColorField(Component fieldNameKey, Color color) {
        return startColorField(fieldNameKey, color.getColor());
    }
    
    TextFieldBuilder startTextField(Component fieldNameKey, String value);
    
    TextDescriptionBuilder startTextDescription(Component value);
    
    <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(Component fieldNameKey, Class<T> clazz, T value);
    
    <T> SelectorBuilder<T> startSelector(Component fieldNameKey, T[] valuesArray, T value);
    
    IntFieldBuilder startIntField(Component fieldNameKey, int value);
    
    LongFieldBuilder startLongField(Component fieldNameKey, long value);
    
    FloatFieldBuilder startFloatField(Component fieldNameKey, float value);
    
    DoubleFieldBuilder startDoubleField(Component fieldNameKey, double value);
    
    IntSliderBuilder startIntSlider(Component fieldNameKey, int value, int min, int max);
    
    IntSliderListBuilder startIntSliderList(Component fieldNameKey, List<Integer> value, int min, int max);

    LongSliderBuilder startLongSlider(Component fieldNameKey, long value, long min, long max);
    
    LongSliderListBuilder startLongSliderList(Component fieldNameKey, List<Long> value, long min, long max);

    KeyCodeBuilder startModifierKeyCodeField(Component fieldNameKey, ModifierKeyCode value);
    
    default KeyCodeBuilder startKeyCodeField(Component fieldNameKey, InputConstants.Key value) {
        return startModifierKeyCodeField(fieldNameKey, ModifierKeyCode.of(value, Modifier.none())).setAllowModifiers(false);
    }
    
    default KeyCodeBuilder fillKeybindingField(Component fieldNameKey, KeyMapping value) {
        return startKeyCodeField(fieldNameKey, value.key).setDefaultValue(value.getDefaultKey()).setSaveConsumer(code -> {
            value.setKey(code);
            KeyMapping.resetMapping();
            Minecraft.getInstance().options.save();
        });
    }
    
    <T> DropdownMenuBuilder<T> startDropdownMenu(Component fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator);
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Component fieldNameKey, SelectionTopCellElement<T> topCellElement) {
        return startDropdownMenu(fieldNameKey, topCellElement, new DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Component fieldNameKey, T value, Function<String, T> toObjectFunction, SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Component fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, Component> toTextFunction, SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toTextFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Component fieldNameKey, T value, Function<String, T> toObjectFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction), new DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Component fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, Component> toTextFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toTextFunction), new DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(Component fieldNameKey, String value, SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, TextComponent::new), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(Component fieldNameKey, String value, Function<String, Component> toTextFunction, SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toTextFunction), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(Component fieldNameKey, String value) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, TextComponent::new), new DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(Component fieldNameKey, String value, Function<String, Component> toTextFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toTextFunction), new DefaultSelectionCellCreator<>());
    }
}
