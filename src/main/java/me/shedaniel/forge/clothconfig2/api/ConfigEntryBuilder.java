package me.shedaniel.forge.clothconfig2.api;

import me.shedaniel.forge.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.forge.clothconfig2.impl.ConfigEntryBuilderImpl;
import me.shedaniel.forge.clothconfig2.impl.builders.*;
import me.shedaniel.forge.clothconfig2.impl.builders.DropdownMenuBuilder.TopCellElementBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public interface ConfigEntryBuilder {
    
    static ConfigEntryBuilder create() {
        return ConfigEntryBuilderImpl.create();
    }
    
    String getResetButtonKey();
    
    ConfigEntryBuilder setResetButtonKey(String resetButtonKey);
    
    IntListBuilder startIntList(String fieldNameKey, List<Integer> value);
    
    LongListBuilder startLongList(String fieldNameKey, List<Long> value);
    
    FloatListBuilder startFloatList(String fieldNameKey, List<Float> value);
    
    DoubleListBuilder startDoubleList(String fieldNameKey, List<Double> value);
    
    StringListBuilder startStrList(String fieldNameKey, List<String> value);
    
    SubCategoryBuilder startSubCategory(String fieldNameKey);
    
    SubCategoryBuilder startSubCategory(String fieldNameKey, List<AbstractConfigListEntry> entries);
    
    BooleanToggleBuilder startBooleanToggle(String fieldNameKey, boolean value);
    
    StringFieldBuilder startStrField(String fieldNameKey, String value);
    
    ColorFieldBuilder startColorField(String fieldNameKey, int value);
    
    default ColorFieldBuilder startAlphaColorField(String fieldNameKey, int value) {
        return startColorField(fieldNameKey, value).setAlphaMode(true);
    }
    
    TextFieldBuilder startTextField(String fieldNameKey, String value);
    
    TextDescriptionBuilder startTextDescription(String value);
    
    <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(String fieldNameKey, Class<T> clazz, T value);
    
    <T> SelectorBuilder<T> startSelector(String fieldNameKey, T[] valuesArray, T value);
    
    IntFieldBuilder startIntField(String fieldNameKey, int value);
    
    LongFieldBuilder startLongField(String fieldNameKey, long value);
    
    FloatFieldBuilder startFloatField(String fieldNameKey, float value);
    
    DoubleFieldBuilder startDoubleField(String fieldNameKey, double value);
    
    IntSliderBuilder startIntSlider(String fieldNameKey, int value, int min, int max);
    
    LongSliderBuilder startLongSlider(String fieldNameKey, long value, long min, long max);
    
    KeyCodeBuilder startModifierKeyCodeField(String fieldNameKey, ModifierKeyCode value);
    
    default KeyCodeBuilder startKeyCodeField(String fieldNameKey, InputMappings.Input value) {
        return startModifierKeyCodeField(fieldNameKey, ModifierKeyCode.of(value, Modifier.none())).setAllowModifiers(false);
    }
    
    default KeyCodeBuilder fillKeybindingField(String fieldNameKey, KeyBinding value) {
        return startKeyCodeField(fieldNameKey, value.getKey()).setDefaultValue(value.getDefault()).setSaveConsumer(code -> {
            value.setKeyModifierAndCode(KeyModifier.NONE, code);
            KeyBinding.updateKeyBindState();
            Minecraft.getInstance().gameSettings.saveOptions();
        });
    }
    
    <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, DropdownBoxEntry.SelectionTopCellElement<T> topCellElement, DropdownBoxEntry.SelectionCellCreator<T> cellCreator);
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, DropdownBoxEntry.SelectionTopCellElement<T> topCellElement) {
        return startDropdownMenu(fieldNameKey, topCellElement, new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, DropdownBoxEntry.SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, Object::toString), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, String> toStringFunction, DropdownBoxEntry.SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toStringFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, Object::toString), new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, String> toStringFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toStringFunction), new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, DropdownBoxEntry.SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, s -> s), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, Function<String, String> toStringFunction, DropdownBoxEntry.SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toStringFunction), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, s -> s), new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, Function<String, String> toStringFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toStringFunction), new DropdownBoxEntry.DefaultSelectionCellCreator<>());
    }
}
