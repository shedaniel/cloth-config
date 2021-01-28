package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.LongSliderEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class LongSliderBuilder extends FieldBuilder<Long, LongSliderEntry> {
    
    private Consumer<Long> saveConsumer = null;
    private Function<Long, Optional<Component[]>> tooltipSupplier = l -> Optional.empty();
    private final long value;
    private final long max;
    private final long min;
    private Function<Long, Component> textGetter = null;
    
    public LongSliderBuilder(Component resetButtonKey, Component fieldNameKey, long value, long min, long max) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
        this.max = max;
        this.min = min;
    }
    
    public LongSliderBuilder setErrorSupplier(Function<Long, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public LongSliderBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public LongSliderBuilder setTextGetter(Function<Long, Component> textGetter) {
        this.textGetter = textGetter;
        return this;
    }
    
    public LongSliderBuilder setSaveConsumer(Consumer<Long> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public LongSliderBuilder setDefaultValue(Supplier<Long> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public LongSliderBuilder setDefaultValue(long defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public LongSliderBuilder setTooltipSupplier(Function<Long, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public LongSliderBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = i -> tooltipSupplier.get();
        return this;
    }
    
    public LongSliderBuilder setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = i -> tooltip;
        return this;
    }
    
    public LongSliderBuilder setTooltip(Component... tooltip) {
        this.tooltipSupplier = i -> Optional.ofNullable(tooltip);
        return this;
    }
    
    
    @NotNull
    @Override
    public LongSliderEntry build() {
        LongSliderEntry entry = new LongSliderEntry(getFieldNameKey(), min, max, value, saveConsumer, getResetButtonKey(), defaultValue, null, isRequireRestart());
        if (textGetter != null)
            entry.setTextGetter(textGetter);
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
