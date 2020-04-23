package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.math.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5251;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ColorFieldBuilder extends FieldBuilder<String, ColorEntry> {
    
    private Consumer<Integer> saveConsumer = null;
    private Function<Integer, Optional<Text>> errorSupplier;
    private Function<Integer, Optional<Text[]>> tooltipSupplier = str -> Optional.empty();
    private final int value;
    private Supplier<Integer> defaultValue;
    private boolean alpha = false;
    
    public ColorFieldBuilder(Text resetButtonKey, Text fieldNameKey, int value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public ColorFieldBuilder setErrorSupplier(Function<Integer, Optional<Text>> errorSupplier) {
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
    
    public ColorFieldBuilder setSaveConsumer3(Consumer<class_5251> saveConsumer) {
        this.saveConsumer = integer -> saveConsumer.accept(class_5251.method_27717(integer));
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
    
    public ColorFieldBuilder setDefaultValue3(Supplier<class_5251> defaultValue) {
        this.defaultValue = () -> defaultValue.get().method_27716();
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
    
    public ColorFieldBuilder setDefaultValue(class_5251 defaultValue) {
        this.defaultValue = () -> Objects.requireNonNull(defaultValue).method_27716();
        return this;
    }
    
    public ColorFieldBuilder setTooltipSupplier(Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = str -> tooltipSupplier.get();
        return this;
    }
    
    public ColorFieldBuilder setTooltipSupplier(Function<Integer, Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public ColorFieldBuilder setTooltip(Optional<Text[]> tooltip) {
        this.tooltipSupplier = str -> tooltip;
        return this;
    }
    
    public ColorFieldBuilder setTooltip(Text... tooltip) {
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
