package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.requirements.ComparisonOperator;
import net.minecraft.network.chat.Component;


public interface ComparativeCondition<T extends Comparable<T>> extends PredicateCondition<T, T> {
    
    @Override
    default boolean predicate(T conditionValue, T testValue) {
        return getRequirement().compare(testValue, conditionValue);
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
