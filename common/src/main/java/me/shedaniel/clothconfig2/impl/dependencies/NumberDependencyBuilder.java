package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.base.Predicate;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ComparisonOperator;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.PredicateConditionBuilder;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.StaticConditionBuilder;

public class NumberDependencyBuilder<T extends Number & Comparable<T>> extends ConfigEntryDependencyBuilder<T, NumberConfigEntry<T>, NumberDependency<T>, NumberDependencyBuilder<T>> {
    
    public NumberDependencyBuilder(NumberConfigEntry<T> gui) {
        super(gui);
    }
    
    @Override
    public NumberDependencyBuilder<T> matching(T value) {
        return matching(new StaticConditionBuilder<>(value).build());
    }
    
    /**
     * Generates a simple {@link Condition condition} that compares the given {@code value} against the depended-on
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
        Predicate<T> predicate = guiValue -> operator.compare(guiValue, value);
        return matching(new PredicateConditionBuilder<>(predicate)
                .setDescription(operator.description(value))
                .setAdjectiveKey("text.cloth-config.dependencies.is")
                .setNegativeAdjectiveKey("text.cloth-config.dependencies.not_is")
                .build());
    }
    
    /**
     * Generates a simple {@link Condition condition} that compares the given {@code gui}'s value against the depended-on
     * config entry's value. The condition is checked using the {@code operator} provided, for example
     * <em>{@code gui_value > other_gui_value}</em>
     * <br><br>
     * The generated condition will be added to the dependency being built.
     *
     * @param otherGui the gui whose value should be compared with the depended-on gui's
     * @return this instance, for chaining
     */
    public NumberDependencyBuilder<T> matching(ComparisonOperator operator, ConfigEntry<T> otherGui) {
        Predicate<T> predicate = guiValue -> operator.compare(guiValue, otherGui.getValue());
        return matching(new PredicateConditionBuilder<>(predicate)
                .setDescription(operator.description(otherGui))
                .setAdjectiveKey("text.cloth-config.dependencies.is")
                .setNegativeAdjectiveKey("text.cloth-config.dependencies.not_is")
                .build());
    }
    
    @Override
    public NumberDependency<T> build() {
        
        // TODO set each condition's formatPrecision to something sensible for the gui's range
        
        return finishBuilding(new NumberDependency<>(this.gui));
    }
}
