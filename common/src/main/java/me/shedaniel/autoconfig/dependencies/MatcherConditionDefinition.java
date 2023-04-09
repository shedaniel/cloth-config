package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Dependency.EnableIf;
import me.shedaniel.autoconfig.util.RelativeI18n;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparisonOperator;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MatcherCondition;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.ComparativeMatcherCondition;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.GenericMatcherCondition;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.StringMatcherCondition;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

record MatcherConditionDefinition(EnumSet<ConditionFlag> flags, @Nullable ComparisonOperator operator, String i18n) {
    
    /**
     * @see StaticConditionDefinition
     * @see EnableIf#matching() Public API documentation
     */
    static MatcherConditionDefinition fromConditionString(String i18nBase, String condition) {
        // Parse any flags
        StaticConditionDefinition definition = StaticConditionDefinition.fromConditionString(condition)
                .map(String::strip);
    
        // Parse any operators
        ComparisonOperator operator = ComparisonOperator.startsWith(definition.condition());
        if (operator != null)
            definition = definition
                    .map(string -> string.substring(operator.toString().length()))
                    .map(String::stripLeading);
    
        // Parse the i18n reference
        definition = definition.map(string -> RelativeI18n.parse(i18nBase, string));
        
        return new MatcherConditionDefinition(definition.flags(), operator, definition.condition());
    }
    
    private boolean inverted() {
        return this.flags().contains(ConditionFlag.INVERTED);
    }
    
    private boolean ignoreCase() {
        return this.flags().contains(ConditionFlag.IGNORE_CASE);
    }
    
    MatcherCondition<String> toStringMatcher(ConfigEntry<String> gui) {
        return new StringMatcherCondition(gui, this.ignoreCase(), this.inverted());
    }
    
    <T> MatcherCondition<T> toMatcher(ConfigEntry<T> gui) {
        return new GenericMatcherCondition<>(gui, this.inverted());
    }
    
    <T extends Comparable<T>> ComparativeMatcherCondition<T> toComparativeMatcher(ConfigEntry<T> gui) {
        return new ComparativeMatcherCondition<>(this.operator(), gui, this.inverted());
    }
}
