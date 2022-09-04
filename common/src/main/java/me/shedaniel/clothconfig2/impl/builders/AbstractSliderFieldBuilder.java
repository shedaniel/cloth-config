package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractSliderFieldBuilder<T, A extends AbstractConfigListEntry, SELF extends FieldBuilder<T, A, SELF>> extends AbstractRangeFieldBuilder<T, A, SELF> {
    protected Function<T, Component> textGetter = null;
    
    protected AbstractSliderFieldBuilder(Component resetButtonKey, Component fieldNameKey) {
        super(resetButtonKey, fieldNameKey);
    }
    
    public SELF setTextGetter(Function<T, Component> textGetter) {
        this.textGetter = textGetter;
        return (SELF) this;
    }
    
    @Override
    public SELF setMin(T min) {
        Objects.requireNonNull(min, "min cannot be null");
        return super.setMin(min);
    }
    
    @Override
    public SELF setMax(T max) {
        Objects.requireNonNull(max, "max cannot be null");
        return super.setMax(max);
    }
}
