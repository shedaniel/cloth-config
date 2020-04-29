package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import me.shedaniel.clothconfig2.impl.builders.*;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder.TopCellElementBuilder;
import me.shedaniel.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.client.keybinding.KeyCodeAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public interface ConfigEntryBuilder {
    
    static ConfigEntryBuilder create() {
        return ConfigEntryBuilderImpl.create();
    }
    
    Text getResetButtonKey();
    
    ConfigEntryBuilder setResetButtonKey(Text resetButtonKey);
    
    IntListBuilder startIntList(Text fieldNameKey, List<Integer> value);
    
    LongListBuilder startLongList(Text fieldNameKey, List<Long> value);
    
    FloatListBuilder startFloatList(Text fieldNameKey, List<Float> value);
    
    DoubleListBuilder startDoubleList(Text fieldNameKey, List<Double> value);
    
    StringListBuilder startStrList(Text fieldNameKey, List<String> value);
    
    SubCategoryBuilder startSubCategory(Text fieldNameKey);
    
    SubCategoryBuilder startSubCategory(Text fieldNameKey, List<AbstractConfigListEntry> entries);
    
    BooleanToggleBuilder startBooleanToggle(Text fieldNameKey, boolean value);
    
    StringFieldBuilder startStrField(Text fieldNameKey, String value);
    
    ColorFieldBuilder startColorField(Text fieldNameKey, int value);
    
    default ColorFieldBuilder startColorField(Text fieldNameKey, TextColor color) {
        return startColorField(fieldNameKey, color.getRgb());
    }
    
    default ColorFieldBuilder startColorField(Text fieldNameKey, Color color) {
        return startColorField(fieldNameKey, color.getColor() & 0xFFFFFF);
    }
    
    default ColorFieldBuilder startAlphaColorField(Text fieldNameKey, int value) {
        return startColorField(fieldNameKey, value).setAlphaMode(true);
    }
    
    default ColorFieldBuilder startAlphaColorField(Text fieldNameKey, Color color) {
        return startColorField(fieldNameKey, color.getColor());
    }
    
    TextFieldBuilder startTextField(Text fieldNameKey, String value);
    
    TextDescriptionBuilder startTextDescription(Text value);
    
    <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(Text fieldNameKey, Class<T> clazz, T value);
    
    <T> SelectorBuilder<T> startSelector(Text fieldNameKey, T[] valuesArray, T value);
    
    IntFieldBuilder startIntField(Text fieldNameKey, int value);
    
    LongFieldBuilder startLongField(Text fieldNameKey, long value);
    
    FloatFieldBuilder startFloatField(Text fieldNameKey, float value);
    
    DoubleFieldBuilder startDoubleField(Text fieldNameKey, double value);
    
    IntSliderBuilder startIntSlider(Text fieldNameKey, int value, int min, int max);
    
    LongSliderBuilder startLongSlider(Text fieldNameKey, long value, long min, long max);
    
    KeyCodeBuilder startModifierKeyCodeField(Text fieldNameKey, ModifierKeyCode value);
    
    default KeyCodeBuilder startKeyCodeField(Text fieldNameKey, InputUtil.KeyCode value) {
        return startModifierKeyCodeField(fieldNameKey, ModifierKeyCode.of(value, Modifier.none())).setAllowModifiers(false);
    }
    
    default KeyCodeBuilder fillKeybindingField(Text fieldNameKey, KeyBinding value) {
        return startKeyCodeField(fieldNameKey, ((KeyCodeAccessor) value).getKeyCode()).setDefaultValue(value.getDefaultKeyCode()).setSaveConsumer(code -> {
            value.setKeyCode(code);
            KeyBinding.updateKeysByCode();
            MinecraftClient.getInstance().options.write();
        });
    }
    
    <T> DropdownMenuBuilder<T> startDropdownMenu(Text fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator);
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Text fieldNameKey, SelectionTopCellElement<T> topCellElement) {
        return startDropdownMenu(fieldNameKey, topCellElement, new DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Text fieldNameKey, T value, Function<String, T> toObjectFunction, SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Text fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, Text> toTextFunction, SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toTextFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Text fieldNameKey, T value, Function<String, T> toObjectFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction), new DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(Text fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, Text> toTextFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toTextFunction), new DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(Text fieldNameKey, String value, SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, LiteralText::new), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(Text fieldNameKey, String value, Function<String, Text> toTextFunction, SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toTextFunction), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(Text fieldNameKey, String value) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, LiteralText::new), new DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(Text fieldNameKey, String value, Function<String, Text> toTextFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toTextFunction), new DefaultSelectionCellCreator<>());
    }
}
