package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;

public abstract class AbstractRangeFieldBuilder<T, A extends AbstractConfigListEntry, SELF extends FieldBuilder<T, A, SELF>> extends AbstractFieldBuilder<T, A, SELF> {
    protected T min = null, max = null;
    
    protected AbstractRangeFieldBuilder(Component resetButtonKey, Component fieldNameKey) {
        super(resetButtonKey, fieldNameKey);
    }
    
    public SELF setMin(T min) {
        this.min = min;
        return (SELF) this;
    }
    
    public SELF setMax(T max) {
        this.max = max;
        return (SELF) this;
    }
    
    public SELF removeMin() {
        this.min = null;
        return (SELF) this;
    }
    
    public SELF removeMax() {
        this.max = null;
        return (SELF) this;
    }
}
