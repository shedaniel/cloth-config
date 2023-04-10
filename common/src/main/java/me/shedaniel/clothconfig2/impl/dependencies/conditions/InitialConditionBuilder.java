package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;

import java.util.function.Predicate;

public class InitialConditionBuilder {
    
    public <T> PredicateConditionBuilder<T> matching(Predicate<T> predicate) {
        return new PredicateConditionBuilder<>(predicate);
    }
    
    public <T> StaticConditionBuilder<T> matching(T value) {
        return new StaticConditionBuilder<>(value);
    }
    
    public <T> MatcherConditionBuilder<T> matching(ConfigEntry<T> gui) {
        return new MatcherConditionBuilder<>(gui);
    }
    
    @SafeVarargs
    public final <T> ListStaticConditionBuilder<T> matching(ContainmentRequirement requirement, T... values) {
        return new ListStaticConditionBuilder<>(requirement, values);
    }
    
    //TODO comparative builders
//    public <T extends Comparable<T>> StaticConditionBuilder<T> matching(ComparisonOperator requirement, T value) {
//        return new ComparativeStaticConditionBuilder<>(value);
//    }
//    
//    public <T extends Comparable<T>> MatcherConditionBuilder<T> matching(ComparisonOperator requirement, ConfigEntry<T> gui) {
//        return new ComparativeMatcherConditionBuilder<>(gui);
//    }
    
    // TODO regex
//    public StaticConditionBuilder<String> matching(String regex) {
//        return new RegexStaticConditionBuilder<>(regex);
//    }
    
    
}
