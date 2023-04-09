package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ComparisonOperator;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.ComparativeMatcherCondition;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.ComparativeStaticCondition;

public class NumberDependencyBuilder<T extends Number & Comparable<T>> extends ConfigEntryDependencyBuilder<T, NumberConfigEntry<T>, NumberDependency<T>, NumberDependencyBuilder<T>> {
    
    public NumberDependencyBuilder(NumberConfigEntry<T> gui) {
        super(gui);
    }
    
    @Override
    public NumberDependencyBuilder<T> matching(T value) {
        return matching(new ComparativeStaticCondition<>(value));
    }
    
    /**
     * Generates a simple {@link ComparativeStaticCondition condition} that compares the given {@code value} against the depended-on
     * config entry's value. The condition is checked using the {@code operator} provided, for example
     * <em>{@code gui_value > value}</em>
     * <br><br>
     * The generated condition will be added to the dependency being built.
     *
     * @param operator an operator defining how the values should be compared
     * @param value a condition value to be checked against the depended-on config entry
     * @return this instance, for chaining
     */
    public NumberDependencyBuilder<T> matching(ComparisonOperator operator, T value) {
        return matching(new ComparativeStaticCondition<>(operator, value));
    }
    
    /**
     * Generates a simple {@link ComparativeMatcherCondition} that compares the given {@code gui}'s value against the depended-on
     * config entry's value. The condition is checked using the {@code operator} provided, for example
     * <em>{@code gui_value > other_gui_value}</em>
     * <br><br>
     * The generated condition will be added to the dependency being built.
     *
     * @param gui the gui whose value should be compared with the depended-on gui's
     * @return this instance, for chaining
     */
    public NumberDependencyBuilder<T> matching(ComparisonOperator operator, ConfigEntry<T> gui) {
        return matching(new ComparativeMatcherCondition<>(operator, gui));
    }
    
    @Override
    public NumberDependency<T> build() {
        
        // TODO set each condition's formatPrecision to something sensible for the gui's range
        
        return finishBuilding(new NumberDependency<>(this.gui));
    }
}
