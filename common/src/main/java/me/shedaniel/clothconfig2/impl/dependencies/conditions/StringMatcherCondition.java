package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;

import java.util.function.BiPredicate;

public class StringMatcherCondition extends AbstractMatcherCondition<String> implements EqualityCondition<String> {
    private final BiPredicate<String, String> predicate;
    
    public StringMatcherCondition(ConfigEntry<String> gui) {
        this(gui, false);
    }
    
    public StringMatcherCondition(ConfigEntry<String> gui, boolean ignoreCase) {
        this(gui, ignoreCase, false);
    }
    
    public StringMatcherCondition(ConfigEntry<String> gui, boolean ignoreCase, boolean inverted) {
        super(gui, inverted);
        this.predicate = ignoreCase ? String::equalsIgnoreCase : Object::equals;
    }
    
    @Override
    public boolean predicate(String conditionValue, String testValue) {
        return predicate.test(conditionValue, testValue);
    }
}
