package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;

public class StringStaticCondition extends AbstractStaticCondition<String> implements EqualityCondition<String> {
    
    public StringStaticCondition(String value) {
        super(value);
    }
}
