package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.MatcherConditionBuilder;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.StaticConditionBuilder;

public interface StartConditionBuilder {
    <T> ConditionBuilder<T, StaticConditionBuilder<T>> matching(T value);
    <T> ConditionBuilder<T, MatcherConditionBuilder<T>> matching(ConfigEntry<T> gui);
    
    // TODO comparison condition builders
//    <T extends Comparable<T>> ConditionBuilder<T, StaticConditionBuilder<T>> matching(ComparisonOperator operator, T value);
//    <T extends Comparable<T>> ConditionBuilder<T, MatcherConditionBuilder<T>> matching(ComparisonOperator operator, ConfigEntry<T> gui);
}
