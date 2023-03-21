package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.NumberCondition;
import me.shedaniel.clothconfig2.api.entries.NumberConfigEntry;

public class NumberDependency<T extends Number & Comparable<T>> extends ComplexDependency<T, NumberCondition<T>, NumberConfigEntry<T>, NumberDependency<T>> {
    
    NumberDependency(NumberConfigEntry<T> entry) {
        super(entry);
    }
    
    @Override
    public NumberDependency<T> withSimpleCondition(T value) {
        addCondition(new NumberCondition<>(value));
        return this;
    }
}
