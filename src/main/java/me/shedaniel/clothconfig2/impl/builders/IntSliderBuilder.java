package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IntSliderBuilder extends FieldBuilder<Integer, IntegerSliderEntry> {
    
    private Consumer<Integer> saveConsumer = null;
    private Function<Integer, Optional<String[]>> tooltipSupplier = i -> Optional.empty();
    private int value, max, min;
    private Function<Integer, String> textGetter = null;
    
    public IntSliderBuilder(String resetButtonKey, String fieldNameKey, int value, int min, int max) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.max = max;
        this.min = min;
    }
    
    public IntSliderBuilder setErrorSupplier(Function<Integer, Optional<String>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public IntSliderBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public IntSliderBuilder setTextGetter(Function<Integer, String> textGetter) {
        this.textGetter = textGetter;
        return this;
    }
    
    public IntSliderBuilder setSaveConsumer(Consumer<Integer> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public IntSliderBuilder setDefaultValue(Supplier<Integer> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public IntSliderBuilder setDefaultValue(int defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public IntSliderBuilder setTooltipSupplier(Function<Integer, Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public IntSliderBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = i -> tooltipSupplier.get();
        return this;
    }
    
    public IntSliderBuilder setTooltip(Optional<String[]> tooltip) {
        this.tooltipSupplier = i -> tooltip;
        return this;
    }
    
    public IntSliderBuilder setTooltip(String... tooltip) {
        this.tooltipSupplier = i -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public IntSliderBuilder setMax(int max) {
        this.max = max;
        return this;
    }
    
    public IntSliderBuilder setMin(int min) {
        this.min = min;
        return this;
    }
    
    @Override
    public IntegerSliderEntry build() {
        IntegerSliderEntry entry = new IntegerSliderEntry(getFieldNameKey(), min, max, value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        if (textGetter != null)
            entry.setTextGetter(textGetter);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}