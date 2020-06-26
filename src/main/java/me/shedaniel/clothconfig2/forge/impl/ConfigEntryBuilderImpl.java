package me.shedaniel.clothconfig2.forge.impl;

import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.forge.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import me.shedaniel.clothconfig2.forge.impl.builders.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ConfigEntryBuilderImpl implements ConfigEntryBuilder {
    
    private ITextComponent resetButtonKey = new TranslationTextComponent("text.cloth-config.reset_value");
    
    private ConfigEntryBuilderImpl() {
    }
    
    public static ConfigEntryBuilderImpl create() {
        return new ConfigEntryBuilderImpl();
    }
    
    public static ConfigEntryBuilderImpl createImmutable() {
        return new ConfigEntryBuilderImpl() {
            @Override
            public ConfigEntryBuilder setResetButtonKey(ITextComponent resetButtonKey) {
                throw new UnsupportedOperationException("This is an immutable entry builder!");
            }
        };
    }
    
    @Override
    public ITextComponent getResetButtonKey() {
        return resetButtonKey;
    }
    
    @Override
    public ConfigEntryBuilder setResetButtonKey(ITextComponent resetButtonKey) {
        this.resetButtonKey = resetButtonKey;
        return this;
    }
    
    @Override
    public IntListBuilder startIntList(ITextComponent fieldNameKey, List<Integer> value) {
        return new IntListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public LongListBuilder startLongList(ITextComponent fieldNameKey, List<Long> value) {
        return new LongListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public FloatListBuilder startFloatList(ITextComponent fieldNameKey, List<Float> value) {
        return new FloatListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public DoubleListBuilder startDoubleList(ITextComponent fieldNameKey, List<Double> value) {
        return new DoubleListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public StringListBuilder startStrList(ITextComponent fieldNameKey, List<String> value) {
        return new StringListBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public SubCategoryBuilder startSubCategory(ITextComponent fieldNameKey) {
        return new SubCategoryBuilder(resetButtonKey, fieldNameKey);
    }
    
    @Override
    public SubCategoryBuilder startSubCategory(ITextComponent fieldNameKey, List<AbstractConfigListEntry> entries) {
        SubCategoryBuilder builder = new SubCategoryBuilder(resetButtonKey, fieldNameKey);
        builder.addAll(entries);
        return builder;
    }
    
    @Override
    public BooleanToggleBuilder startBooleanToggle(ITextComponent fieldNameKey, boolean value) {
        return new BooleanToggleBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public StringFieldBuilder startStrField(ITextComponent fieldNameKey, String value) {
        return new StringFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public ColorFieldBuilder startColorField(ITextComponent fieldNameKey, int value) {
        return new ColorFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public TextFieldBuilder startTextField(ITextComponent fieldNameKey, String value) {
        return new TextFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public TextDescriptionBuilder startTextDescription(ITextComponent value) {
        return new TextDescriptionBuilder(resetButtonKey, new StringTextComponent(UUID.randomUUID().toString()), value);
    }
    
    @Override
    public <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(ITextComponent fieldNameKey, Class<T> clazz, T value) {
        return new EnumSelectorBuilder<>(resetButtonKey, fieldNameKey, clazz, value);
    }
    
    @Override
    public <T> SelectorBuilder<T> startSelector(ITextComponent fieldNameKey, T[] valuesArray, T value) {
        return new SelectorBuilder<>(resetButtonKey, fieldNameKey, valuesArray, value);
    }
    
    @Override
    public IntFieldBuilder startIntField(ITextComponent fieldNameKey, int value) {
        return new IntFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public LongFieldBuilder startLongField(ITextComponent fieldNameKey, long value) {
        return new LongFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public FloatFieldBuilder startFloatField(ITextComponent fieldNameKey, float value) {
        return new FloatFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public DoubleFieldBuilder startDoubleField(ITextComponent fieldNameKey, double value) {
        return new DoubleFieldBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public IntSliderBuilder startIntSlider(ITextComponent fieldNameKey, int value, int min, int max) {
        return new IntSliderBuilder(resetButtonKey, fieldNameKey, value, min, max);
    }
    
    @Override
    public LongSliderBuilder startLongSlider(ITextComponent fieldNameKey, long value, long min, long max) {
        return new LongSliderBuilder(resetButtonKey, fieldNameKey, value, min, max);
    }
    
    @Override
    public KeyCodeBuilder startModifierKeyCodeField(ITextComponent fieldNameKey, ModifierKeyCode value) {
        return new KeyCodeBuilder(resetButtonKey, fieldNameKey, value);
    }
    
    @Override
    public <T> DropdownMenuBuilder<T> startDropdownMenu(ITextComponent fieldNameKey, SelectionTopCellElement<T> topCellElement, SelectionCellCreator<T> cellCreator) {
        return new DropdownMenuBuilder<>(resetButtonKey, fieldNameKey, topCellElement, cellCreator);
    }
    
}
