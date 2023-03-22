package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.NumberCondition;
import me.shedaniel.clothconfig2.api.entries.NumberConfigEntry;

public class NumberDependency<T extends Number & Comparable<T>> extends ComplexDependency<T, NumberCondition<T>, NumberConfigEntry<T>> {
    
    public NumberDependency(NumberConfigEntry<T> entry) {
        super(entry);
    }
    
}
