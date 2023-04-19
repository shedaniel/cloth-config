package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;

import java.util.function.Predicate;

public class PredicateConditionBuilder<T> extends AbstractConditionBuilder<T, PredicateConditionBuilder<T>> {
    
    private final Predicate<T> predicate;
    
    public PredicateConditionBuilder(Predicate<T> predicate) {
        this.predicate = predicate;
    }
    
    @Override
    public Condition<T> build() {
        if (this.describer == null && this.description == null)
            throw new IllegalArgumentException("Either a description or a describer function must be defined");
        
        Predicate<T> predicate = this.inverted ? value -> !this.predicate.test(value) : this.predicate;
        return this.description == null ?
                new PredicateCondition<>(predicate, this.describer)
              : new PredicateCondition<>(predicate, this.description);
    }
}
