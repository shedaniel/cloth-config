package me.shedaniel.clothconfig2.api.dependencies.conditions;

public interface PredicateCondition<T, U> extends Condition<U> {
    
    boolean predicate(T conditionValue, U testValue);
    
    @Override
    default boolean check(U value) {
        return inverted() != predicate(getValue(), value);
    }
    
    T getValue();
}
