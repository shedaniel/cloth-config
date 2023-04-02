package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;

import java.util.EnumSet;

public abstract class FlaggedCondition<T> implements Condition<T> {
    
    private final EnumSet<ConditionFlag> flags = EnumSet.noneOf(ConditionFlag.class);
    
    public EnumSet<ConditionFlag> getFlags() {
        return flags;
    }
    
    @Override
    public boolean inverted() {
        return getFlags().contains(ConditionFlag.INVERTED);
    }
    
    public void setFlags(EnumSet<ConditionFlag> flags) {
        getFlags().addAll(flags);
    }
    
    public void setFlags(String flags) {
        setFlags(ConditionFlag.parseFlags(flags));
    }
    
    public void setFlags() {
        setFlags(ConditionFlag.ALL);
    }
    
    public void resetFlags(EnumSet<ConditionFlag> flags) {
        getFlags().removeAll(flags);
    }
    
    public void resetFlags(String flags) {
        resetFlags(ConditionFlag.parseFlags(flags));
    }
    
    public void resetFlags() {
        resetFlags(ConditionFlag.ALL);
    }
    
    public void flipFlags(EnumSet<ConditionFlag> flags) {
        flags.forEach(flag -> {
            if (getFlags().contains(flag))
                getFlags().remove(flag);
            else
                getFlags().add(flag);
        });
    }
    
    public void flipFlags(String flags) {
        flipFlags(ConditionFlag.parseFlags(flags));
    }
}
