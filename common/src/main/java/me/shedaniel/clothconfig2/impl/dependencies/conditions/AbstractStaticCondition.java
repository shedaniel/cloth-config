package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.PredicateCondition;

public abstract class AbstractStaticCondition<T> extends AbstractCondition<T> implements PredicateCondition<T, T> {
    
    private final T value;
    
    protected AbstractStaticCondition(T value, boolean inverted) {
        super(inverted);
        this.value = value;
    }
    
    @Override
    public T getValue() {
        return value;
    }
}
