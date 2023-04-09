package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;

public class GenericMatcherCondition<T> extends AbstractMatcherCondition<T> implements EqualityCondition<T> {
    
    public GenericMatcherCondition(ConfigEntry<T> gui) {
        this(gui, false);
    }
    
    public GenericMatcherCondition(ConfigEntry<T> gui, boolean inverted) {
        super(gui, inverted);
    }
}
