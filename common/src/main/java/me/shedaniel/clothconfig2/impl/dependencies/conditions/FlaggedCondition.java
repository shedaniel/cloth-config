package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConditionFlag;

import java.util.EnumSet;

public abstract class FlaggedCondition<T> implements Condition<T> {
    
    private final EnumSet<ConditionFlag> flags = EnumSet.noneOf(ConditionFlag.class);
    
    @Override
    public EnumSet<ConditionFlag> getFlags() {
        return flags;
    }
    
}
