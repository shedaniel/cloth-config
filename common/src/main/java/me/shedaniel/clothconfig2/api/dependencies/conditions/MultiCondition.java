package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;

import java.util.Collection;

public interface MultiCondition<T> extends Condition<Collection<T>> {
    
    @Override
    default boolean check(Collection<T> values) {
        return inverted() != getRequirement().check(getValue(), values);
    }
    
    ContainmentRequirement getRequirement();
    
}
