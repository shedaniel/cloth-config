package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;
import java.util.function.BiPredicate;

public interface EqualityCondition<T> extends Condition<T> {
    
    default BiPredicate<T, T> getPredicate() {
        return Objects::equals;
    }
    
    @Override
    default boolean check(T value) {
        return inverted() != getPredicate().test(getValue(), value);
    }
    
    @Override
    default Component getText(boolean inverted) {
        // TODO make use of getValueString()??
        MutableComponent text = Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                Component.translatable("text.cloth-config.quoted", Component.literal(getValue().toString())));
    
        return inverted() ? Component.translatable("text.cloth-config.dependencies.conditions.not", text) : text;
    }
}
