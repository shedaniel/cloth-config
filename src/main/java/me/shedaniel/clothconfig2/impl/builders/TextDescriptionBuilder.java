package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TextDescriptionBuilder extends FieldBuilder<Component, TextListEntry> {
    
    private int color = -1;
    @Nullable private Supplier<Optional<Component[]>> tooltipSupplier = null;
    private final Component value;
    
    public TextDescriptionBuilder(Component resetButtonKey, Component fieldNameKey, Component value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    @Override
    public void requireRestart(boolean requireRestart) {
        throw new UnsupportedOperationException();
    }
    
    public TextDescriptionBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public TextDescriptionBuilder setTooltip(Optional<Component[]> tooltip) {
        this.tooltipSupplier = () -> tooltip;
        return this;
    }
    
    public TextDescriptionBuilder setTooltip(Component... tooltip) {
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
