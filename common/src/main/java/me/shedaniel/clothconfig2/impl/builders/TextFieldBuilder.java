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
public class TextFieldBuilder extends FieldBuilder<String, StringListEntry> {
    
    private Consumer<String> saveConsumer = null;
    private Function<String, Optional<Component[]>> tooltipSupplier = str -> Optional.empty();
    private final String value;
    
    public TextFieldBuilder(Component resetButtonKey, Component fieldNameKey, String value) {
        super(resetButtonKey, fieldNameKey);
        Objects.requireNonNull(value);
        this.value = value;
    }
    
    public TextFieldBuilder setErrorSupplier(Function<String, Optional<Component>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public TextFieldBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public TextFieldBuilder setSaveConsumer(Consumer<String> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public TextFieldBuilder setDefaultValue(Supplier<String> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public TextFieldBuilder setDefaultValue(String defaultValue) {
        this.defaultValue = () -> Objects.requireNonNull(defaultValue);
        return this;
    }
    
    public TextFieldBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = str -> tooltipSupplier.get();
        return this;
    }
    
    public TextFieldBuilder setTooltipSupplier(Function<String, Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public TextFieldBuilder setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = str -> tooltip;
        return this;
    }
    
    public TextFieldBuilder setTooltip(Component... tooltip) {
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
