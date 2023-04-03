package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;

public interface MatcherCondition<T> extends Condition<T> {
    
    @Override
    default T getValue() {
        return getElement().getValue();
    }
    
    ConfigEntry<T> getElement();
}
