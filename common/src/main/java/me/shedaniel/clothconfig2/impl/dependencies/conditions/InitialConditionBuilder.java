package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;

import java.util.function.Predicate;

public class InitialConditionBuilder {
    
    public <T> StaticConditionBuilder<T> matching(T value) {
        return new StaticConditionBuilder<>(value);
    }
    
    public <T> MatcherConditionBuilder<T> matching(ConfigEntry<T> gui) {
        return new MatcherConditionBuilder<>(gui);
    }
    
    public <T> PredicateConditionBuilder<T> matching(Predicate<T> predicate) {
        return new PredicateConditionBuilder<>(predicate);
    }
}
