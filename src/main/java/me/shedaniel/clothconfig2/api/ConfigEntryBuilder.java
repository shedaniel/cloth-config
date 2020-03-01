package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import me.shedaniel.clothconfig2.impl.builders.*;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder.TopCellElementBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.client.keybinding.KeyCodeAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
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
    
    default KeyCodeBuilder startKeyCodeField(String fieldNameKey, InputUtil.KeyCode value) {
        return startModifierKeyCodeField(fieldNameKey, ModifierKeyCode.of(value, Modifier.none())).setAllowModifiers(false);
    }
    
    default KeyCodeBuilder fillKeybindingField(String fieldNameKey, KeyBinding value) {
        return startKeyCodeField(fieldNameKey, ((KeyCodeAccessor) value).getKeyCode()).setDefaultValue(value.getDefaultKeyCode()).setSaveConsumer(code -> {
            value.setKeyCode(code);
            KeyBinding.updateKeysByCode();
            MinecraftClient.getInstance().options.write();
        });
    }
    
    <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator);
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, SelectionTopCellElement<T> topCellElement) {
        return startDropdownMenu(fieldNameKey, topCellElement, new DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, Object::toString), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, String> toStringFunction, SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toStringFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, Object::toString), new DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(String fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, String> toStringFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toStringFunction), new DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, s -> s), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, Function<String, String> toStringFunction, SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toStringFunction), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, s -> s), new DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(String fieldNameKey, String value, Function<String, String> toStringFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toStringFunction), new DefaultSelectionCellCreator<>());
    }
}
