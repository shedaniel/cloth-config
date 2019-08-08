package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FieldBuilder<T, A extends AbstractConfigListEntry> {
    private final String fieldNameKey;
    private final String resetButtonKey;
    protected boolean requireRestart = false;
    protected Supplier<T> defaultValue = null;
    protected Function<T, Optional<String>> errorSupplier;
    
    protected FieldBuilder(String resetButtonKey, String fieldNameKey) {
        this.resetButtonKey = resetButtonKey;
        this.fieldNameKey = fieldNameKey;
    }
    
    public final Supplier<T> getDefaultValue() {
        return defaultValue;
    }
    
    public final AbstractConfigListEntry buildEntry() {
        return build();
    }
    
    public abstract A build();
    
    public final String getFieldNameKey() {
        return fieldNameKey;
    }
    
    public final String getResetButtonKey() {
        return resetButtonKey;
    }
    
    public boolean isRequireRestart() {
        return requireRestart;
    }
    
    public void requireRestart(boolean requireRestart) {
        this.requireRestart = requireRestart;
    }
    
}