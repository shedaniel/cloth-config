package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.LongListEntry;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LongFieldBuilder extends FieldBuilder<Long> {
    
    private Consumer<Long> saveConsumer = null;
    private Supplier<Optional<String[]>> tooltipSupplier = null;
    private long value;
    private Long min = null, max = null;
    
    public LongFieldBuilder(String resetButtonKey, String fieldNameKey, long value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public LongFieldBuilder setSaveConsumer(Consumer<Long> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public LongFieldBuilder setDefaultValue(Supplier<Long> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public LongFieldBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public LongFieldBuilder setMin(long min) {
        this.min = min;
        return this;
    }
    
    public LongFieldBuilder setMax(long max) {
        this.max = max;
        return this;
    }
    
    public LongFieldBuilder removeMin() {
        this.min = null;
        return this;
    }
    
    public LongFieldBuilder removeMax() {
        this.max = null;
        return this;
    }
    
    @Override
    public AbstractConfigListEntry buildEntry() {
        LongListEntry entry = new LongListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, tooltipSupplier);
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        return entry;
    }
    
}