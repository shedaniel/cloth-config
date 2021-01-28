package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class StringFieldBuilder extends FieldBuilder<String, StringListEntry> {
    
    private Consumer<String> saveConsumer = null;
    private Function<String, Optional<Component[]>> tooltipSupplier = str -> Optional.empty();
    private final String value;
    
    public StringFieldBuilder(Component resetButtonKey, Component fieldNameKey, String value) {
        super(resetButtonKey, fieldNameKey);
        Objects.requireNonNull(value);
        this.value = value;
    }
    
    public StringFieldBuilder setErrorSupplier(Function<String, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public StringFieldBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public StringFieldBuilder setSaveConsumer(Consumer<String> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public StringFieldBuilder setDefaultValue(Supplier<String> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public StringFieldBuilder setDefaultValue(String defaultValue) {
        this.defaultValue = () -> Objects.requireNonNull(defaultValue);
        return this;
    }
    
    public StringFieldBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = str -> tooltipSupplier.get();
        return this;
    }
    
    public StringFieldBuilder setTooltipSupplier(Function<String, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public StringFieldBuilder setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = str -> tooltip;
        return this;
    }
    
    public StringFieldBuilder setTooltip(Component... tooltip) {
        this.tooltipSupplier = str -> Optional.ofNullable(tooltip);
        return this;
    }
    
    @NotNull
    @Override
    public StringListEntry build() {
        StringListEntry entry = new StringListEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
