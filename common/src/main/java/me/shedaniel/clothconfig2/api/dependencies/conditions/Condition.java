package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

public interface Condition<T> {
    
    boolean check(T value);
    
    Component description();
}
