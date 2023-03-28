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
    
    
    Component getText(boolean inverted);
    
    default boolean inverted() {
        return getFlags().contains(ConditionFlag.INVERTED);
    }
    
    EnumSet<ConditionFlag> getFlags();
    
    default void setFlags(EnumSet<ConditionFlag> flags) {
        getFlags().addAll(flags);
    }
    
    default void setFlags(String flags) {
        setFlags(ConditionFlag.parseFlags(flags));
    }
    
    default void setFlags() {
        setFlags(ConditionFlag.ALL);
    }
    
    default void resetFlags(EnumSet<ConditionFlag> flags) {
        getFlags().removeAll(flags);
    }
    
    default void resetFlags(String flags) {
        resetFlags(ConditionFlag.parseFlags(flags));
    }
    
    default void resetFlags() {
        resetFlags(ConditionFlag.ALL);
    }
    
    default void flipFlags(EnumSet<ConditionFlag> flags) {
        flags.forEach(flag -> {
            if (getFlags().contains(flag))
                getFlags().remove(flag);
            else
                getFlags().add(flag);
        });
    }
    
    default void flipFlags(String flags) {
        flipFlags(ConditionFlag.parseFlags(flags));
    }
}
