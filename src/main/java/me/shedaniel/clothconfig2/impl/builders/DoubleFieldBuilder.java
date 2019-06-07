package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DoubleFieldBuilder extends FieldBuilder<Double> {
    
    private Consumer<Double> saveConsumer = null;
    private Supplier<Optional<String[]>> tooltipSupplier = null;
    private double value;
    private Double min = null, max = null;
    
    public DoubleFieldBuilder(String resetButtonKey, String fieldNameKey, double value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public DoubleFieldBuilder setSaveConsumer(Consumer<Double> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public DoubleFieldBuilder setDefaultValue(Supplier<Double> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public DoubleFieldBuilder setMin(double min) {
        this.min = min;
        return this;
    }
    
    public DoubleFieldBuilder setMax(double max) {
        this.max = max;
        return this;
    }
    
    public DoubleFieldBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public DoubleFieldBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    public DoubleFieldBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    @Override
    public AbstractConfigListEntry buildEntry() {
        DoubleListEntry entry = new DoubleListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, tooltipSupplier);
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        return entry;
    }
    
}