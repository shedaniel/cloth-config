package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;

import java.util.Collection;

public interface MultiCondition<T> extends PredicateCondition<Collection<T>, Collection<T>> {
    
    @Override
    default boolean predicate(Collection<T> conditionValues, Collection<T> testValues) {
        return getRequirement().check(conditionValues, testValues);
    }
    
    ContainmentRequirement getRequirement();
}
