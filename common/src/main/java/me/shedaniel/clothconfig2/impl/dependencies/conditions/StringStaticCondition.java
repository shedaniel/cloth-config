package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;

import java.util.function.BiPredicate;

public class StringStaticCondition extends AbstractStaticCondition<String> implements EqualityCondition<String> {
    
    private final BiPredicate<String, String> predicate;
    
    public StringStaticCondition(String value) {
        this(value, false);
    }
    
    public StringStaticCondition(String value, boolean ignoreCase) {
        this(value, ignoreCase, false);
    }
    
    public StringStaticCondition(String value, boolean ignoreCase, boolean inverted) {
        super(value, inverted);
        this.predicate = ignoreCase ? String::equalsIgnoreCase : Object::equals;
    }
    
    @Override
    public boolean predicate(String conditionValue, String testValue) {
        return predicate.test(conditionValue, testValue);
    }
}
