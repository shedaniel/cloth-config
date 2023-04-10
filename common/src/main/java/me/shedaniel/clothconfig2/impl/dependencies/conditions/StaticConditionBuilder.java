package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public class StaticConditionBuilder<T> extends SimpleConditionBuilder<T, StaticConditionBuilder<T>> {
    
    private final T staticValue;
    
    public StaticConditionBuilder(T value) {
        this.staticValue = value;
    }
    
    @Override
    protected Predicate<T> buildPredicate() {
        return this.staticValue::equals;
    }
    
    @Override
    protected Component buildDescription() {
        if (this.staticValue instanceof Boolean bool)
            return buildDescription(bool);
        return buildDescription(this.staticValue);
    }
    
    private Component buildDescription(Boolean value) {
        return Component.translatable("text.cloth-config.dependencies.conditions.%s"
                .formatted(this.inverted != value ? "enabled" : "disabled"));
    }
    
    private Component buildDescription(T value) {
        Component description = Component.translatable("text.cloth-config.dependencies.conditions.set_to", 
                Component.translatable("text.cloth-config.quoted", Component.literal(String.valueOf(value))));
    
        return this.inverted ? Component.translatable("text.cloth-config.dependencies.conditions.not", description) : description;
    
    }
}
