package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConditionBuilder;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public abstract class AbstractConditionBuilder<T, SELF extends AbstractConditionBuilder<T, SELF>> implements ConditionBuilder<T, SELF> {
    
    protected boolean inverted = false;
    protected Component description = null;
    protected Supplier<Component> describer = null;
    protected String adjectiveKey;
    protected String negativeAdjectiveKey;
    
    @Override
    public SELF setInverted() {
        return setInverted(true);
    }
    
    @Override
    public SELF setInverted(boolean inverted) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.inverted = inverted;
        return self;
    }
    
    @Override
    public SELF setAdjectiveKey(String key) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.adjectiveKey = key;
        return self;
    }
    
    @Override
    public SELF setNegativeAdjectiveKey(String key) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.negativeAdjectiveKey = key;
        return self;
    }
    
    @Override
    public SELF setDescription(Component description) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.description = description;
        return self;
    }
    
    @Override
    public SELF setDescriber(Supplier<Component> describer) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.describer = describer;
        return self;
    }
    
    public SELF describeUsing(ConfigEntry<T> gui) {
        throw new IllegalArgumentException("Unsupported operation");
    }
}
