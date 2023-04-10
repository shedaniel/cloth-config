package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public interface ConditionBuilder<T, SELF> {
    
    NewCondition<T> build();
    
    SELF setInverted();
    
    SELF setInverted(boolean inverted);
    
    SELF setDescription(Component description);
    
    SELF setDescriber(Supplier<Component> describer);
}
