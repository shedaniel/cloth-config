package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntFieldBuilder extends FieldBuilder<Integer> {
    
    private Consumer<Integer> saveConsumer = null;
    private Supplier<Optional<String[]>> tooltipSupplier = null;
    private int value;
    private Integer min = null, max = null;
    
    public IntFieldBuilder(String resetButtonKey, String fieldNameKey, int value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public IntFieldBuilder setSaveConsumer(Consumer<Integer> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public IntFieldBuilder setDefaultValue(Supplier<Integer> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public IntFieldBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public IntFieldBuilder setMin(int min) {
        this.min = min;
        return this;
    }
    
    public IntFieldBuilder setMax(int max) {
        this.max = max;
        return this;
    }
    
    public IntFieldBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public IntFieldBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    @Override
    public AbstractConfigListEntry buildEntry() {
        IntegerListEntry entry = new IntegerListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, tooltipSupplier);
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        return entry;
    }
    
}