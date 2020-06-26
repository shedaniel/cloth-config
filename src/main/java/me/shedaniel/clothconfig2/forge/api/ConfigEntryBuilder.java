package me.shedaniel.clothconfig2.forge.api;

import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import me.shedaniel.clothconfig2.forge.impl.ConfigEntryBuilderImpl;
import me.shedaniel.clothconfig2.forge.impl.builders.*;
import me.shedaniel.clothconfig2.forge.impl.builders.DropdownMenuBuilder.TopCellElementBuilder;
import me.shedaniel.math.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
    
    ITextComponent getResetButtonKey();
    
    ConfigEntryBuilder setResetButtonKey(ITextComponent resetButtonKey);
    
    IntListBuilder startIntList(ITextComponent fieldNameKey, List<Integer> value);
    
    LongListBuilder startLongList(ITextComponent fieldNameKey, List<Long> value);
    
    FloatListBuilder startFloatList(ITextComponent fieldNameKey, List<Float> value);
    
    DoubleListBuilder startDoubleList(ITextComponent fieldNameKey, List<Double> value);
    
    StringListBuilder startStrList(ITextComponent fieldNameKey, List<String> value);
    
    SubCategoryBuilder startSubCategory(ITextComponent fieldNameKey);
    
    SubCategoryBuilder startSubCategory(ITextComponent fieldNameKey, List<AbstractConfigListEntry> entries);
    
    BooleanToggleBuilder startBooleanToggle(ITextComponent fieldNameKey, boolean value);
    
    StringFieldBuilder startStrField(ITextComponent fieldNameKey, String value);
    
    ColorFieldBuilder startColorField(ITextComponent fieldNameKey, int value);
    
    default ColorFieldBuilder startColorField(ITextComponent fieldNameKey, net.minecraft.util.text.Color color) {
        return startColorField(fieldNameKey, color.func_240742_a_());
    }
    
    default ColorFieldBuilder startColorField(ITextComponent fieldNameKey, Color color) {
        return startColorField(fieldNameKey, color.getColor() & 0xFFFFFF);
    }
    
    default ColorFieldBuilder startAlphaColorField(ITextComponent fieldNameKey, int value) {
        return startColorField(fieldNameKey, value).setAlphaMode(true);
    }
    
    default ColorFieldBuilder startAlphaColorField(ITextComponent fieldNameKey, Color color) {
        return startColorField(fieldNameKey, color.getColor());
    }
    
    TextFieldBuilder startTextField(ITextComponent fieldNameKey, String value);
    
    TextDescriptionBuilder startTextDescription(ITextComponent value);
    
    <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(ITextComponent fieldNameKey, Class<T> clazz, T value);
    
    <T> SelectorBuilder<T> startSelector(ITextComponent fieldNameKey, T[] valuesArray, T value);
    
    IntFieldBuilder startIntField(ITextComponent fieldNameKey, int value);
    
    LongFieldBuilder startLongField(ITextComponent fieldNameKey, long value);
    
    FloatFieldBuilder startFloatField(ITextComponent fieldNameKey, float value);
    
    DoubleFieldBuilder startDoubleField(ITextComponent fieldNameKey, double value);
    
    IntSliderBuilder startIntSlider(ITextComponent fieldNameKey, int value, int min, int max);
    
    LongSliderBuilder startLongSlider(ITextComponent fieldNameKey, long value, long min, long max);
    
    KeyCodeBuilder startModifierKeyCodeField(ITextComponent fieldNameKey, ModifierKeyCode value);
    
    default KeyCodeBuilder startKeyCodeField(ITextComponent fieldNameKey, InputMappings.Input value) {
        return startModifierKeyCodeField(fieldNameKey, ModifierKeyCode.of(value, Modifier.none())).setAllowModifiers(false);
    }
    
    default KeyCodeBuilder fillKeybindingField(ITextComponent fieldNameKey, KeyBinding value) {
        return startKeyCodeField(fieldNameKey, value.getKey()).setDefaultValue(value.getDefault()).setSaveConsumer(code -> {
            value.setKeyModifierAndCode(KeyModifier.NONE, code);
            KeyBinding.updateKeyBindState();
            Minecraft.getInstance().gameSettings.saveOptions();
        });
    }
    
    <T> DropdownMenuBuilder<T> startDropdownMenu(ITextComponent fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator);
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(ITextComponent fieldNameKey, SelectionTopCellElement<T> topCellElement) {
        return startDropdownMenu(fieldNameKey, topCellElement, new DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(ITextComponent fieldNameKey, T value, Function<String, T> toObjectFunction, SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(ITextComponent fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, ITextComponent> toTextFunction, SelectionCellCreator<T> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toTextFunction), cellCreator);
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(ITextComponent fieldNameKey, T value, Function<String, T> toObjectFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction), new DefaultSelectionCellCreator<>());
    }
    
    default <T> DropdownMenuBuilder<T> startDropdownMenu(ITextComponent fieldNameKey, T value, Function<String, T> toObjectFunction, Function<T, ITextComponent> toTextFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, toObjectFunction, toTextFunction), new DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(ITextComponent fieldNameKey, String value, SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, StringTextComponent::new), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(ITextComponent fieldNameKey, String value, Function<String, ITextComponent> toTextFunction, SelectionCellCreator<String> cellCreator) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toTextFunction), cellCreator);
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(ITextComponent fieldNameKey, String value) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, StringTextComponent::new), new DefaultSelectionCellCreator<>());
    }
    
    default DropdownMenuBuilder<String> startStringDropdownMenu(ITextComponent fieldNameKey, String value, Function<String, ITextComponent> toTextFunction) {
        return startDropdownMenu(fieldNameKey, TopCellElementBuilder.of(value, s -> s, toTextFunction), new DefaultSelectionCellCreator<>());
    }
}
