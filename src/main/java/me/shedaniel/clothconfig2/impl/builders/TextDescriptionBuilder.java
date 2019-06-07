package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.TextListEntry;

import java.util.Optional;
import java.util.function.Supplier;

public class TextDescriptionBuilder extends FieldBuilder {
    
    private int color = -1;
    private Supplier<Optional<String[]>> tooltipSupplier = null;
    private String value;
    
    public TextDescriptionBuilder(String resetButtonKey, String fieldNameKey, String value) {
        super(resetButtonKey, fieldNameKey);
        this.value = value;
    }
    
    public TextDescriptionBuilder setTooltipSupplier(Supplier<Optional<String[]>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }
    
    public TextDescriptionBuilder setColor(int color) {
        this.color = color;
        return this;
    }
    
    @Override
    public AbstractConfigListEntry buildEntry() {
        return new TextListEntry(getFieldNameKey(), value, color, tooltipSupplier);
    }
    
}