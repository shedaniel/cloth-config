package me.shedaniel.clothconfig2.api.dependencies.builders;

import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.NumberDependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.NumberCondition;
import org.jetbrains.annotations.ApiStatus;

public class NumberDependencyBuilder<T extends Number & Comparable<T>> extends MultiConditionDependencyBuilder<T, NumberConfigEntry<T>, NumberCondition<T>, NumberDependency<T>, NumberDependencyBuilder<T>> {
    
    @ApiStatus.Internal
    @Deprecated
    public NumberDependencyBuilder(NumberConfigEntry<T> gui) {
        super(gui, 1);
    }
    
    @Override
    public NumberDependencyBuilder<T> withCondition(T value) {
        return withCondition(new NumberCondition<>(value));
    }
    
    @Override
    public NumberDependency<T> build() {
        
        // TODO set each condition's formatPrecision to something sensible for the gui's range
        
        return finishBuilding(new NumberDependency<>(this.gui));
    }
}
