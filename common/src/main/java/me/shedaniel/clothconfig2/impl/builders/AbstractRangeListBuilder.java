package me.shedaniel.clothconfig2.impl.builders;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.network.chat.Component;

public abstract class AbstractRangeListBuilder<T, A extends AbstractConfigListEntry, SELF extends AbstractRangeListBuilder<T, A, SELF>> extends AbstractListBuilder<T, A, SELF> {
    protected T min = null, max = null;
    
    protected AbstractRangeListBuilder(Component resetButtonKey, Component fieldNameKey) {
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
