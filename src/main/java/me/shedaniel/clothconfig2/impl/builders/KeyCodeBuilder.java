package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.Modifier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import net.minecraft.client.util.InputUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class KeyCodeBuilder extends FieldBuilder<ModifierKeyCode, KeyCodeEntry> {
    
    @Nullable private Consumer<ModifierKeyCode> saveConsumer = null;
    @Nonnull private Function<ModifierKeyCode, Optional<String[]>> tooltipSupplier = bool -> Optional.empty();
    private ModifierKeyCode value;
    private boolean allowKey = true, allowMouse = true, allowModifiers = true;
    
    public KeyCodeBuilder(String resetButtonKey, String fieldNameKey, ModifierKeyCode value) {
        super(resetButtonKey, fieldNameKey);
        this.value = ModifierKeyCode.copyOf(value);
    }
    
    public KeyCodeBuilder setAllowModifiers(boolean allowModifiers) {
        this.allowModifiers = allowModifiers;
        if (!allowModifiers)
            value.setModifier(Modifier.none());
        return this;
    }
    
    public KeyCodeBuilder setAllowKey(boolean allowKey) {
        if (!allowMouse && !allowKey)
            throw new IllegalArgumentException();
        this.allowKey = allowKey;
        return this;
    }
    
    public KeyCodeBuilder setAllowMouse(boolean allowMouse) {
        if (!allowKey && !allowMouse)
            throw new IllegalArgumentException();
        this.allowMouse = allowMouse;
        return this;
    }
    
    public KeyCodeBuilder setErrorSupplier(@Nullable Function<InputUtil.KeyCode, Optional<String>> errorSupplier) {
        return setModifierErrorSupplier(keyCode -> errorSupplier.apply(keyCode.getKeyCode()));
    }
    
    public KeyCodeBuilder setModifierErrorSupplier(@Nullable Function<ModifierKeyCode, Optional<String>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public KeyCodeBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public KeyCodeBuilder setSaveConsumer(Consumer<InputUtil.KeyCode> saveConsumer) {
        return setModifierSaveConsumer(keyCode -> saveConsumer.accept(keyCode.getKeyCode()));
    }
    
    public KeyCodeBuilder setDefaultValue(Supplier<InputUtil.KeyCode> defaultValue) {
        return setModifierDefaultValue(() -> ModifierKeyCode.of(defaultValue.get(), Modifier.none()));
    }
    
    public KeyCodeBuilder setModifierSaveConsumer(Consumer<ModifierKeyCode> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public KeyCodeBuilder setModifierDefaultValue(Supplier<ModifierKeyCode> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public KeyCodeBuilder setDefaultValue(InputUtil.KeyCode defaultValue) {
        return setDefaultValue(ModifierKeyCode.of(defaultValue, Modifier.none()));
    }
    
    public KeyCodeBuilder setDefaultValue(ModifierKeyCode defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public KeyCodeBuilder setTooltipSupplier(@Nonnull Function<InputUtil.KeyCode, Optional<String[]>> tooltipSupplier) {
        return setModifierTooltipSupplier(keyCode -> tooltipSupplier.apply(keyCode.getKeyCode()));
    }
    
    public KeyCodeBuilder setModifierTooltipSupplier(@Nonnull Function<ModifierKeyCode, Optional<String[]>> tooltipSupplier) {
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
        entry.setAllowKey(allowKey);
        entry.setAllowMouse(allowMouse);
        entry.setAllowModifiers(allowModifiers);
        return entry;
    }
    
}