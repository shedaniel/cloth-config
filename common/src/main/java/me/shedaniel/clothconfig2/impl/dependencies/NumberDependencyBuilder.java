package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.NumberCondition;

public class NumberDependencyBuilder<T extends Number & Comparable<T>> extends MultiConditionDependencyBuilder<T, NumberConfigEntry<T>, NumberDependency<T>, NumberDependencyBuilder<T>> {
    
    public NumberDependencyBuilder(NumberConfigEntry<T> gui) {
        super(gui);
    }
    
    @Override
    public NumberDependencyBuilder<T> matching(T value) {
        return matching(new NumberCondition<>(value));
    }
    
    @Override
    public NumberDependency<T> build() {
        
        // TODO set each condition's formatPrecision to something sensible for the gui's range
        
        return finishBuilding(new NumberDependency<>(this.gui));
    }
}
