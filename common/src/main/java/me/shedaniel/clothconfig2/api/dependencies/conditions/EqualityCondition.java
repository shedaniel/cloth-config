package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface EqualityCondition<T> extends Condition<T> {
    
    @Override
    default boolean check(T value) {
        return getValue().equals(value);
    }
    
    @Override
    default Component getText(boolean inverted) {
        // TODO make use of getValueString()??
        MutableComponent text = Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                Component.translatable("text.cloth-config.quoted", Component.literal(getValue().toString())));
    
        return inverted() ? Component.translatable("text.cloth-config.dependencies.conditions.not", text) : text;
    }
}
