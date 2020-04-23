package me.shedaniel.clothconfig2.impl;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ConfigEntryBuilderImpl implements ConfigEntryBuilder {
    
    private Text resetButtonKey = new TranslatableText("text.cloth-config.reset_value");
    
    private ConfigEntryBuilderImpl() {
    }
    
    public static ConfigEntryBuilderImpl create() {
        return new ConfigEntryBuilderImpl();
    }
    
    public static ConfigEntryBuilderImpl createImmutable() {
        return new ConfigEntryBuilderImpl() {
            @Override
            public ConfigEntryBuilder setResetButtonKey(Text resetButtonKey) {
                throw new UnsupportedOperationException("This is an immutable entry builder!");
            }
        };
    }
    
    @Override
    public Text getResetButtonKey() {
        return resetButtonKey;
    }
    
    @Override
    public ConfigEntryBuilder setResetButtonKey(Text resetButtonKey) {
        this.resetButtonKey = resetButtonKey;
        return this;
    }
    
    @Override
    public IntListBuilder startIntList(Text fieldNameKey, List<Integer> value) {
        return new IntListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public LongListBuilder startLongList(Text fieldNameKey, List<Long> value) {
        return new LongListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public FloatListBuilder startFloatList(Text fieldNameKey, List<Float> value) {
        return new FloatListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public DoubleListBuilder startDoubleList(Text fieldNameKey, List<Double> value) {
        return new DoubleListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public StringListBuilder startStrList(Text fieldNameKey, List<String> value) {
        return new StringListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public SubCategoryBuilder startSubCategory(Text fieldNameKey) {
        return new SubCategoryBuilder(resetButtonKey, fieldNameKey);
    }
    
    @Override
    public SubCategoryBuilder startSubCategory(Text fieldNameKey, List<AbstractConfigListEntry> entries) {
        SubCategoryBuilder builder = new SubCategoryBuilder(resetButtonKey, fieldNameKey);
        builder.addAll(entries);
        return builder;
    }
    
    @Override
    public BooleanToggleBuilder startBooleanToggle(Text fieldNameKey, boolean value) {
        return new BooleanToggleBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public StringFieldBuilder startStrField(Text fieldNameKey, String value) {
        return new StringFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public ColorFieldBuilder startColorField(Text fieldNameKey, int value) {
        return new ColorFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public TextFieldBuilder startTextField(Text fieldNameKey, String value) {
        return new TextFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public TextDescriptionBuilder startTextDescription(Text value) {
        return new TextDescriptionBuilder(resetButtonKey, new LiteralText(UUID.randomUUID().toString()), value);
    }
    
    @Override
    public <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(Text fieldNameKey, Class<T> clazz, T value) {
        return new EnumSelectorBuilder<>(resetButtonKey, fieldNameKey, clazz, value);
    }
    
    @Override
    public <T> SelectorBuilder<T> startSelector(Text fieldNameKey, T[] valuesArray, T value) {
        return new SelectorBuilder<>(resetButtonKey, fieldNameKey, valuesArray, value);
    }
    
    @Override
    public IntFieldBuilder startIntField(Text fieldNameKey, int value) {
        return new IntFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public LongFieldBuilder startLongField(Text fieldNameKey, long value) {
        return new LongFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public FloatFieldBuilder startFloatField(Text fieldNameKey, float value) {
        return new FloatFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public DoubleFieldBuilder startDoubleField(Text fieldNameKey, double value) {
        return new DoubleFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public IntSliderBuilder startIntSlider(Text fieldNameKey, int value, int min, int max) {
        return new IntSliderBuilder(resetButtonKey, fieldNameKey, value, min, max);
    }
    
    @Override
    public LongSliderBuilder startLongSlider(Text fieldNameKey, long value, long min, long max) {
        return new LongSliderBuilder(resetButtonKey, fieldNameKey, value, min, max);
    }
    
    @Override
    public KeyCodeBuilder startModifierKeyCodeField(Text fieldNameKey, ModifierKeyCode value) {
        return new KeyCodeBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public <T> DropdownMenuBuilder<T> startDropdownMenu(Text fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator) {
        return new DropdownMenuBuilder<>(resetButtonKey, fieldNameKey, topCellElement, cellCreator);
    }
    
}
