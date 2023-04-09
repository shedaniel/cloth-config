package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

public interface Condition<T> {
    
    /**
     * Checks if the condition is met by the provided value.
     * 
     * @param value the value to check against this condition
     * @return whether {@code value} satisfies this condition
     */
    boolean check(T value);
    
    Component getText(boolean inverted);
    
    boolean inverted();
}
