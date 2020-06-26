package me.shedaniel.clothconfig2.forge.impl.builders;

import me.shedaniel.clothconfig2.forge.gui.entries.ColorEntry;
import me.shedaniel.math.Color;
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
public class ColorFieldBuilder extends FieldBuilder<String, ColorEntry> {
    
    private Consumer<Integer> saveConsumer = null;
    private Function<Integer, Optional<ITextComponent>> errorSupplier;
    private Function<Integer, Optional<ITextComponent[]>> tooltipSupplier = str -> Optional.empty();
    private final int value;
    private Supplier<Integer> defaultValue;
    private boolean alpha = false;
    
    public ColorFieldBuilder(ITextComponent resetButtonKey, ITextComponent fieldNameKey, int value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public ColorFieldBuilder setErrorSupplier(Function<Integer, Optional<ITextComponent>> errorSupplier) {
        this.errorSupplier = errorSupplier;
        return this;
    }
    
    public ColorFieldBuilder requireRestart() {
        requireRestart(true);
        return this;
    }
    
    public ColorFieldBuilder setSaveConsumer(Consumer<Integer> saveConsumer) {
        this.saveConsumer = saveConsumer;
        return this;
    }
    
    public ColorFieldBuilder setSaveConsumer2(Consumer<Color> saveConsumer) {
        this.saveConsumer = integer -> saveConsumer.accept(alpha ? Color.ofTransparent(integer) : Color.ofOpaque(integer));
        return this;
    }
    
    public ColorFieldBuilder setSaveConsumer3(Consumer<net.minecraft.util.text.Color> saveConsumer) {
        this.saveConsumer = integer -> saveConsumer.accept(net.minecraft.util.text.Color.func_240743_a_(integer));
        return this;
    }
    
    public ColorFieldBuilder setDefaultValue(Supplier<Integer> defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public ColorFieldBuilder setDefaultValue2(Supplier<Color> defaultValue) {
        this.defaultValue = () -> defaultValue.get().getColor();
        return this;
    }
    
    public ColorFieldBuilder setDefaultValue3(Supplier<net.minecraft.util.text.Color> defaultValue) {
        this.defaultValue = () -> defaultValue.get().func_240742_a_();
        return this;
    }
    
    public ColorFieldBuilder setAlphaMode(boolean withAlpha) {
        this.alpha = withAlpha;
        return this;
    }
    
    public ColorFieldBuilder setDefaultValue(int defaultValue) {
        this.defaultValue = () -> defaultValue;
        return this;
    }
    
    public ColorFieldBuilder setDefaultValue(net.minecraft.util.text.Color defaultValue) {
        this.defaultValue = () -> Objects.requireNonNull(defaultValue).func_240742_a_();
        return this;
    }
    
    public ColorFieldBuilder setTooltipSupplier(Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = str -> tooltipSupplier.get();
        return this;
    }
    
    public ColorFieldBuilder setTooltipSupplier(Function<Integer, Optional<ITextComponent[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public ColorFieldBuilder setTooltip(Optional<ITextComponent[]> tooltip) {
        this.tooltipSupplier = str -> tooltip;
        return this;
    }
    
    public ColorFieldBuilder setTooltip(ITextComponent... tooltip) {
        this.tooltipSupplier = str -> Optional.ofNullable(tooltip);
        return this;
    }
    
    @NotNull
    @Override
    public ColorEntry build() {
        ColorEntry entry = new ColorEntry(getFieldNameKey(), value, getResetButtonKey(), defaultValue, saveConsumer, null, isRequireRestart());
        if (this.alpha) {
            entry.withAlpha();
        } else {
            entry.withoutAlpha();
        }
        entry.setTooltipSupplier(() -> tooltipSupplier.apply(entry.getValue()));
        if (errorSupplier != null)
            entry.setErrorSupplier(() -> errorSupplier.apply(entry.getValue()));
        return entry;
    }
    
}
