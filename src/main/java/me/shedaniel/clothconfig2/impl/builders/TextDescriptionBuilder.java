package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TextDescriptionBuilder extends FieldBuilder<Text, TextListEntry> {
    
    private int color = -1;
    @Nullable private Supplier<Optional<Text[]>> tooltipSupplier = null;
    private final Text value;
    
    public TextDescriptionBuilder(Text resetButtonKey, Text fieldNameKey, Text value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public void requireRestart(boolean requireRestart) {
        throw new UnsupportedOperationException();
    }
    
    public TextDescriptionBuilder setTooltipSupplier(Supplier<Optional<Text[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public TextDescriptionBuilder setTooltip(Optional<Text[]> tooltip) {
        this.tooltipSupplier = () -> tooltip;
        return this;
    }
    
    public TextDescriptionBuilder setTooltip(Text... tooltip) {
        this.tooltipSupplier = () -> Optional.ofNullable(tooltip);
        return this;
    }
    
    public TextDescriptionBuilder setColor(int color) {
        this.color = color;
        return this;
    }
    
    @NotNull
    @Override
    public TextListEntry build() {
        return new TextListEntry(getFieldNameKey(), value, color, tooltipSupplier);
    }
    
}
