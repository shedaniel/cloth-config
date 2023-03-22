package me.shedaniel.clothconfig2.api.dependencies.builders;

import me.shedaniel.clothconfig2.api.dependencies.NumberDependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.NumberCondition;
import me.shedaniel.clothconfig2.api.entries.NumberConfigEntry;
import org.jetbrains.annotations.ApiStatus;

public class NumberDependencyBuilder<T extends Number & Comparable<T>> extends AbstractDependencyBuilder<T, NumberConfigEntry<T>, NumberDependency<T>, NumberCondition<T>, NumberDependencyBuilder<T>> {
    
    @ApiStatus.Internal
    @Deprecated
    public NumberDependencyBuilder(NumberConfigEntry<T> gui) {
        super(gui);
    }
    
    @Override
    public NumberDependencyBuilder<T> withCondition(T value) {
        return withCondition(new NumberCondition<>(value));
    }
    
    @Override
    public NumberDependency<T> build() {
        if (conditions.isEmpty())
            throw new IllegalArgumentException("Number dependencies require at least one condition.");
        
        return finishBuilding(new NumberDependency<>(this.gui));
    }
}
