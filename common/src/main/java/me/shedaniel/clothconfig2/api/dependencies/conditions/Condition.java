package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.EnumSet;

public interface Condition<T> {
    
    /**
     * Checks if the condition is met by the provided value.
     * 
     * @param value the value to check against this condition
     * @return whether {@code value} satisfies this condition
     */
    boolean check(T value);
    
    /**
     * Gets the value this condition currently requires.
     * 
     * @return the current value required by this condition
     */
    T getValue();
    
    Component getText(boolean inverted);
    
    EnumSet<ConditionFlag> getFlags();
    
    default boolean inverted() {
        return hasFlag(ConditionFlag.INVERTED);
    }
    
    default boolean hasFlag(ConditionFlag flag) {
        return getFlags().contains(flag);
    }
    
    default void setFlags(EnumSet<ConditionFlag> flags) {
        getFlags().addAll(flags);
    }
    
    default void resetFlags(EnumSet<ConditionFlag> flags) {
        getFlags().removeAll(flags);
    }
}
