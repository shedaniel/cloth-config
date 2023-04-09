package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;

public abstract class AbstractCondition<T> implements Condition<T> {
    protected final boolean inverted;
    
    protected AbstractCondition(boolean inverted) {this.inverted = inverted;}
    
    @Override
    public boolean inverted() {
        return this.inverted;
    }
}
