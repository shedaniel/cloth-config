package me.shedaniel.clothconfig2.api;

import me.shedaniel.clothconfig2.impl.ConfigEntryBuilderImpl;
import me.shedaniel.clothconfig2.impl.builders.*;

import java.util.List;

public interface ConfigEntryBuilder {
    
    static ConfigEntryBuilder create() {
        return ConfigEntryBuilderImpl.create();
    }
    
    String getResetButtonKey();
    
    ConfigEntryBuilder setResetButtonKey(String resetButtonKey);
    
    SubCategoryBuilder startSubCategory(String fieldNameKey);
    
    SubCategoryBuilder startSubCategory(String fieldNameKey, List<AbstractConfigListEntry> entries);
    
    BooleanToggleBuilder startBooleanToggle(String fieldNameKey, boolean value);
    
    TextFieldBuilder startTextField(String fieldNameKey, String value);
    
    TextDescriptionBuilder startTextDescription(String value);
    
    <T extends Enum<?>> EnumSelectorBuilder<T> startEnumSelector(String fieldNameKey, Class<T> clazz, T value);
    
    IntFieldBuilder startIntField(String fieldNameKey, int value);
    
    LongFieldBuilder startLongField(String fieldNameKey, long value);
    
    FloatFieldBuilder startFloatField(String fieldNameKey, float value);
    
    DoubleFieldBuilder startDoubleField(String fieldNameKey, double value);
    
    IntSliderBuilder startIntSlider(String fieldNameKey, int value, int min, int max);
    
    LongSliderBuilder startLongSlider(String fieldNameKey, long value, long min, long max);
}
