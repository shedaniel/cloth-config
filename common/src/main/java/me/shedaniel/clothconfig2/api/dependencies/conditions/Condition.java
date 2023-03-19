package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

public abstract class Condition<T> {
    
    protected final T value;
    protected Condition(T value) {
        this.value = value;
    }
    
    public abstract boolean check(T value);
    
    public abstract Component getText();
    
    // FIXME need to override equals() and hashCode() for ComplexDependency.equals() to work correctly
}
