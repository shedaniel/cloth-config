package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface EqualityCondition<T> extends PredicateCondition<T, T> {
    
    @Override
    default boolean predicate(T conditionValue, T testValue) {
        return conditionValue.equals(testValue);
    }
    
    @Override
    default Component getText(boolean inverted) {
        // TODO make use of getValueString()??
        MutableComponent text = Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                Component.translatable("text.cloth-config.quoted", Component.literal(getValue().toString())));
    
        return inverted() ? Component.translatable("text.cloth-config.dependencies.conditions.not", text) : text;
    }
}
