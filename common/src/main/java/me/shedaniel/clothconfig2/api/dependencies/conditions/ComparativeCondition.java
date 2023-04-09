package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;


public interface ComparativeCondition<T extends Comparable<T>> extends Condition<T> {
    
    @Override
    default boolean check(T value) {
        return inverted() != getRequirement().compare(value, getValue());
    }
    
    @Override
    default Component getText(boolean inverted) {
        ComparisonOperator operator = getRequirement().inverted(inverted != inverted());
    
        return Component.translatable("text.cloth-config.dependencies.conditions.%s"
                .formatted(operator.name().toLowerCase()), getStringValue());
    }
    
    String getStringValue();
    
    ComparisonOperator getRequirement();
    
    void setFormatPrecision(int places);
    
    int formatPrecision();
    
}
