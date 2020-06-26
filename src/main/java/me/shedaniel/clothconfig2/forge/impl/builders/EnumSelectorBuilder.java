package me.shedaniel.clothconfig2.forge.impl.builders;

import me.shedaniel.clothconfig2.forge.gui.entries.EnumListEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class EnumSelectorBuilder<T extends Enum<?>> extends FieldBuilder<T, EnumListEntry<T>> {
    
    private Consumer<T> saveConsumer = null;
    private Function<T, Optional<ITextComponent[]>> tooltipSupplier = e -> Optional.empty();
    private final T value;
    private final Class<T> clazz;
    private Function<Enum, ITextComponent> enumNameProvider = EnumListEntry.DEFAULT_NAME_PROVIDER;
    
    public EnumSelectorBuilder(ITextComponent resetButtonKey, ITextComponent fieldNameKey, Class<T> clazz, T value) {
        super(resetButtonKey, fieldNameKey);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(value);
        this.value = value;
        this.clazz = clazz;
    }
    
    public EnumSelectorBuilder<T> setErrorSupplier(Function<T, Optional<ITextComponent>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public EnumSelectorBuilder<T> requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public EnumSelectorBuilder<T> setSaveConsumer(Consumer<T> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public EnumSelectorBuilder<T> setDefaultValue(Supplier<T> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public EnumSelectorBuilder<T> setDefaultValue(T defaultValue) {
        Objects.requireNonNull(defaultValue);
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public EnumSelectorBuilder<T> setTooltipSupplier(Function<T, Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public EnumSelectorBuilder<T> setTooltipSupplier(Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = e -> tooltipSupplier.get();
        return this;
    }
    
    public EnumSelectorBuilder<T> setTooltip(Optional<ITextComponent[]> tooltip) {
        this.tooltipSupplier = e -> tooltip;
        return this;
    }
    
    public EnumSelectorBuilder<T> setTooltip(ITextComponent... tooltip) {
        this.tooltipSupplier = e -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public EnumSelectorBuilder<T> setEnumNameProvider(Function<Enum, ITextComponent> enumNameProvider) {
        Objects.requireNonNull(enumNameProvider);
        this.enumNameProvider = enumNameProvider;
        return this;
    }
    
    @NotNull
    @Override
    public EnumListEntry<T> build() {
        EnumListEntry<T> entry = new EnumListEntry<>(getFieldNameKey(), clazz, value, getResetButtonKey(), defaultValue, saveConsumer, enumNameProvider, null, isRequireRestart());
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
