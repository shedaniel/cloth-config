package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import java.util.function.Supplier;

public abstract class FieldBuilder<T> {
    private final String fieldNameKey;
    private final String resetButtonKey;
    protected Supplier<T> defaultValue = null;
    
    protected FieldBuilder(String resetButtonKey, String fieldNameKey) {
        this.resetButtonKey = resetButtonKey;
        this.fieldNameKey = fieldNameKey;
    }
    
    public final Supplier<T> getDefaultValue() {
        return defaultValue;
    }
    
    public abstract AbstractConfigListEntry buildEntry();
    
    public final String getFieldNameKey() {
        return fieldNameKey;
    }
    
    public final String getResetButtonKey() {
        return resetButtonKey;
    }
    
}