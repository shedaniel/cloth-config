package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import org.jetbrains.annotations.Nullable;

public class ComparativeConfigEntryMatcher<T extends Comparable<T>> extends ConfigEntryMatcher<T> {
    
    private final ComparisonOperator operator;
    
    public ComparativeConfigEntryMatcher(ConfigEntry<T> gui) {
        this(gui, null);
    }
    
    public ComparativeConfigEntryMatcher(ConfigEntry<T> gui, @Nullable ComparisonOperator operator) {
        super(gui);
        this.operator = operator == null ? ComparisonOperator.EQUAL : operator;
    }
    
    @Override
    public boolean matches(T value) {
        return operator.compare(value, getElement().getValue());
    }
}
