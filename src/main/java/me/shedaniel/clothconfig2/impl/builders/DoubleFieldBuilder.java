package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleFieldBuilder extends FieldBuilder<Double, DoubleListEntry> {
    
    private Consumer<Double> saveConsumer = null;
    private Function<Double, Optional<String[]>> tooltipSupplier = d -> Optional.empty();
    private final double value;
    private Double min = null, max = null;
    
    public DoubleFieldBuilder(String resetButtonKey, String fieldNameKey, double value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public DoubleFieldBuilder setErrorSupplier(Function<Double, Optional<String>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public DoubleFieldBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public DoubleFieldBuilder setSaveConsumer(Consumer<Double> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public DoubleFieldBuilder setDefaultValue(Supplier<Double> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public DoubleFieldBuilder setDefaultValue(double defaultValue) {
        this.defaultValue = () -> defaultValue;
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
    
    public DoubleFieldBuilder setTooltipSupplier(Function<Double, Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public DoubleFieldBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = d -> tooltipSupplier.get();
        return this;
    }
    
    public DoubleFieldBuilder setTooltip(Optional<String[]> tooltip) {
        this.tooltipSupplier = d -> tooltip;
        return this;
    }
    
    public DoubleFieldBuilder setTooltip(String... tooltip) {
        this.tooltipSupplier = d -> Optional.ofNullable(tooltip);
        return this;
    }
    
    @NotNull
    @Override
    public DoubleListEntry build() {
        DoubleListEntry entry = new DoubleListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        if (min != null)
            entry.setMinimum(min);
        if (max != null)
            entry.setMaximum(max);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
