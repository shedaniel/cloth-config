package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparativeCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparisonOperator;
import org.jetbrains.annotations.Nullable;

public class ComparativeMatcherCondition<T extends Comparable<T>> extends AbstractMatcherCondition<T> implements ComparativeCondition<T> {
    
    private final ComparisonOperator operator;
    
    public ComparativeMatcherCondition(ConfigEntry<T> gui) {
        this(null, gui);
    }
    
    public ComparativeMatcherCondition(@Nullable ComparisonOperator operator, ConfigEntry<T> gui) {
        this(operator, gui, false);
    }
    public ComparativeMatcherCondition(@Nullable ComparisonOperator operator, ConfigEntry<T> gui, boolean inverted) {
        super(gui, inverted);
        this.operator = operator == null ? ComparisonOperator.EQUAL : operator;
    }
    
    @Override
    public void setFormatPrecision(int places) {
        // TODO
    }
    
    @Override
    public int formatPrecision() {
        // TODO
        return 0;
    }
    
    @Override
    public ComparisonOperator getRequirement() {
        return this.operator;
    }
    
    @Override
    public String getStringValue() {
        // TODO we actually want to return a translatable Component here... Need to rework Comparativecondition#getText() and the lang file
        return null;
    }
}
