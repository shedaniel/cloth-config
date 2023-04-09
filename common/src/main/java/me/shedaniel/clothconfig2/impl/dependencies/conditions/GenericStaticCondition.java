package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;


public class GenericStaticCondition<T> extends AbstractStaticCondition<T> implements EqualityCondition<T> {
    
    public GenericStaticCondition(T value) {
        this(value, false);
    }
    public GenericStaticCondition(T value, boolean inverted) {
        super(value, inverted);
    }
}
