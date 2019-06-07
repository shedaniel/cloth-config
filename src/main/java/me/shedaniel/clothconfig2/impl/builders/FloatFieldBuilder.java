package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.FloatListEntry;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FloatFieldBuilder extends FieldBuilder<Float> {
    
    private Consumer<Float> saveConsumer = null;
    private Supplier<Optional<String[]>> tooltipSupplier = null;
    private float value;
    private Float min = null, max = null;
    
    public FloatFieldBuilder(String resetButtonKey, String fieldNameKey, float value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public FloatFieldBuilder setSaveConsumer(Consumer<Float> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public FloatFieldBuilder setDefaultValue(Supplier<Float> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public FloatFieldBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public FloatFieldBuilder setMin(float min) {
        this.min = min;
        return this;
    }
    
    public FloatFieldBuilder setMax(float max) {
        this.max = max;
        return this;
    }
    
    public FloatFieldBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public FloatFieldBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    @Override
    public AbstractConfigListEntry buildEntry() {
        FloatListEntry entry = new FloatListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, tooltipSupplier);
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        return entry;
    }
    
}