package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import net.minecraft.client.util.InputUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class KeyCodeBuilder extends FieldBuilder<InputUtil.KeyCode, KeyCodeEntry> {
    
    @Nullable private Consumer<InputUtil.KeyCode> saveConsumer = null;
    @Nonnull private Function<InputUtil.KeyCode, Optional<String[]>> tooltipSupplier = bool -> Optional.empty();
    private InputUtil.KeyCode value;
    
    public KeyCodeBuilder(String resetButtonKey, String fieldNameKey, InputUtil.KeyCode value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public KeyCodeBuilder setErrorSupplier(@Nullable Function<InputUtil.KeyCode, Optional<String>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public KeyCodeBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public KeyCodeBuilder setSaveConsumer(Consumer<InputUtil.KeyCode> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public KeyCodeBuilder setDefaultValue(Supplier<InputUtil.KeyCode> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public KeyCodeBuilder setDefaultValue(InputUtil.KeyCode defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public KeyCodeBuilder setTooltipSupplier(@Nonnull Function<InputUtil.KeyCode, Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public KeyCodeBuilder setTooltipSupplier(@Nonnull Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = bool -> tooltipSupplier.get();
        return this;
    }
    
    public KeyCodeBuilder setTooltip(Optional<String[]> tooltip) {
        this.tooltipSupplier = bool -> tooltip;
        return this;
    }
    
    public KeyCodeBuilder setTooltip(@Nullable String... tooltip) {
        this.tooltipSupplier = bool -> Optional.ofNullable(tooltip);
        return this;
    }
    
    @Override
    public KeyCodeEntry build() {
        KeyCodeEntry entry = new KeyCodeEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}