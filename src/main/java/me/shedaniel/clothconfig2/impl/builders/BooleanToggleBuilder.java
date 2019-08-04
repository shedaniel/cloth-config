package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BooleanToggleBuilder extends FieldBuilder<Boolean, BooleanListEntry> {
    
    private Consumer<Boolean> saveConsumer = null;
    private Function<Boolean, Optional<String[]>> tooltipSupplier = bool -> Optional.empty();
    private boolean value;
    private Function<Boolean, String> yesNoTextSupplier = bool -> bool ? "§aYes" : "§cNo";
    
    public BooleanToggleBuilder(String resetButtonKey, String fieldNameKey, boolean value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public BooleanToggleBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public BooleanToggleBuilder setSaveConsumer(Consumer<Boolean> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public BooleanToggleBuilder setDefaultValue(Supplier<Boolean> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public BooleanToggleBuilder setDefaultValue(boolean defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public BooleanToggleBuilder setTooltipSupplier(Function<Boolean, Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public BooleanToggleBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = bool -> tooltipSupplier.get();
        return this;
    }
    
    public BooleanToggleBuilder setTooltip(Optional<String[]> tooltip) {
        this.tooltipSupplier = bool -> tooltip;
        return this;
    }
    
    public BooleanToggleBuilder setTooltip(String... tooltip) {
        this.tooltipSupplier = bool -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public BooleanToggleBuilder setYesNoTextSupplier(Function<Boolean, String> yesNoTextSupplier) {
        Objects.requireNonNull(yesNoTextSupplier);
        this.yesNoTextSupplier = yesNoTextSupplier;
        return this;
    }
    
    @Override
    public BooleanListEntry build() {
        BooleanListEntry entry = new BooleanListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart()) {
            @Override
            public String getYesNoText(boolean bool) {
                return yesNoTextSupplier.apply(bool);
            }
        };
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        return entry;
    }
    
}