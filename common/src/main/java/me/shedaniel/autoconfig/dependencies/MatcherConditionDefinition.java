package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Dependency.EnableIf;
import me.shedaniel.autoconfig.util.RelativeI18n;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparativeConfigEntryMatcher;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparisonOperator;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConditionFlag;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConfigEntryMatcher;
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
    
    <T extends Comparable<T>> ComparativeConfigEntryMatcher<T> toComparableMatcher(ConfigEntry<T> gui) {
        ComparativeConfigEntryMatcher<T> matcher = new ComparativeConfigEntryMatcher<>(gui, this.operator());
        matcher.setFlags(this.flags());
        return matcher;
    }
    
    <T> ConfigEntryMatcher<T> toMatcher(ConfigEntry<T> gui) {
        ConfigEntryMatcher<T> matcher = new ConfigEntryMatcher<>(gui);
        matcher.setFlags(this.flags());
        return matcher;
    }
}
