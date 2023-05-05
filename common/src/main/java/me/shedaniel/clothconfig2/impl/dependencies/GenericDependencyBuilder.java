package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.StaticConditionBuilder;

public class GenericDependencyBuilder<T> extends ConfigEntryDependencyBuilder<T, GenericDependencyBuilder<T>> {
    
    public <E extends ConfigEntry<T>> GenericDependencyBuilder(E gui) {
        super(gui);
    }
    
    @Override
    public GenericDependencyBuilder<T> matching(T value) {
        return matching(new StaticConditionBuilder<>(value).build());
    }
}
