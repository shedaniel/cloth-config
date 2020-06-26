package me.shedaniel.clothconfig2.forge.impl.builders;

import me.shedaniel.clothconfig2.forge.gui.entries.DoubleListEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class DoubleFieldBuilder extends FieldBuilder<Double, DoubleListEntry> {
    
    private Consumer<Double> saveConsumer = null;
    private Function<Double, Optional<ITextComponent[]>> tooltipSupplier = d -> Optional.empty();
    private final double value;
    private Double min = null, max = null;
    
    public DoubleFieldBuilder(ITextComponent resetButtonKey, ITextComponent fieldNameKey, double value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public DoubleFieldBuilder setErrorSupplier(Function<Double, Optional<ITextComponent>> errorSupplier) {
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
    
    public DoubleFieldBuilder setTooltipSupplier(Function<Double, Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public DoubleFieldBuilder setTooltipSupplier(Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = d -> tooltipSupplier.get();
        return this;
    }
    
    public DoubleFieldBuilder setTooltip(Optional<ITextComponent[]> tooltip) {
        this.tooltipSupplier = d -> tooltip;
        return this;
    }
    
    public DoubleFieldBuilder setTooltip(ITextComponent... tooltip) {
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
