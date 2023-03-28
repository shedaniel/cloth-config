package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.NumberConfigEntry;

public class NumberDependency<T extends Number & Comparable<T>> extends ConfigEntryDependency<T, NumberConfigEntry<T>> {
    
    NumberDependency(NumberConfigEntry<T> entry) {
        super(entry);
    }
    
}
