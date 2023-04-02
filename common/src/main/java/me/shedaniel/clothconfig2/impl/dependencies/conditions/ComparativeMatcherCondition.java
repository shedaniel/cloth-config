package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparisonOperator;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MatcherCondition;
import org.jetbrains.annotations.Nullable;

public class ComparativeMatcherCondition<T extends Comparable<T>> extends MatcherCondition<T> {
    
    private final ComparisonOperator operator;
    
    public ComparativeMatcherCondition(ConfigEntry<T> gui) {
        this(null, gui);
    }
    
    public ComparativeMatcherCondition(@Nullable ComparisonOperator operator, ConfigEntry<T> gui) {
        super(gui);
        this.operator = operator == null ? ComparisonOperator.EQUAL : operator;
    }
    
    @Override
    public boolean matches(T value) {
        return operator.compare(value, getElement().getValue());
    }
}
