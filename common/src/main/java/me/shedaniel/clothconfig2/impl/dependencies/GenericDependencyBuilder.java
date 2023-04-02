package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.GenericCondition;

public class GenericDependencyBuilder<T> extends ConfigEntryDependencyBuilder<T, ConfigEntry<T>, GenericDependency<T>, GenericDependencyBuilder<T>> {
    
    public <E extends ConfigEntry<T>> GenericDependencyBuilder(E gui) {
        super(gui);
    }
    
    @Override
    public GenericDependency<T> build() {
        return finishBuilding(new GenericDependency<>(this.gui));
    }
    
    @Override
    public GenericDependencyBuilder<T> matching(T value) {
        return matching(new GenericCondition<>(value));
    }
}
