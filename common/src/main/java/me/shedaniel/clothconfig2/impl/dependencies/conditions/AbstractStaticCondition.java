package me.shedaniel.clothconfig2.impl.dependencies.conditions;

public abstract class AbstractStaticCondition<T> extends FlaggedCondition<T> {
    
    private final T value;
    
    protected AbstractStaticCondition(T value) {
        this.value = value;
    }
    
    @Override
    public T getValue() {
        return value;
    }
}
