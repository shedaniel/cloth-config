package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.NumberCondition;

public class NumberDependency<T extends Number & Comparable<T>> extends ConfigEntryDependency<T, NumberConfigEntry<T>, NumberCondition<T>> {
    
    NumberDependency(NumberConfigEntry<T> entry) {
        super(entry);
    }
    
}
