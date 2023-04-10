package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.NewCondition;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public abstract class SimpleConditionBuilder<T, SELF extends SimpleConditionBuilder<T, SELF>> extends AbstractConditionBuilder<T, SELF> {
    @Override
    public NewCondition<T> build() {
        if (this.describer == null && this.description == null)
            this.description = buildDescription();
    
        Predicate<T> predicate = this.buildPredicate();
        return new PredicateConditionBuilder<>(predicate)
                .setInverted(this.inverted)
                .setDescription(this.description)
                .setDescriber(this.describer)
                .build();
    }
    
    protected abstract Predicate<T> buildPredicate();
    
    protected abstract Component buildDescription();
}
