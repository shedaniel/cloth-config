package me.shedaniel.clothconfig2.impl.dependencies.conditions;

public abstract class AbstractStaticCondition<T> extends AbstractCondition<T> {
    
    private final T value;
    
    protected AbstractStaticCondition(T value, boolean inverted) {
        super(inverted);
        this.value = value;
    }
    
    @Override
    public T getValue() {
        return value;
    }
}
