package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Predicate;

public class ListStaticConditionBuilder<T> extends ListConditionBuilder<T> {
    
    
    
    private final List<T> values;
    
    @SafeVarargs
    public ListStaticConditionBuilder(ContainmentRequirement requirement, T... values) {
        this(requirement, List.of(values));
    }
    public ListStaticConditionBuilder(ContainmentRequirement requirement, List<T> values) {
        super(requirement);
        this.values = values;
    }
    
    @Override
    protected Predicate<List<T>> buildPredicate() {
        return list -> this.requirement.check(list, this.values);
    }
    
    @Override
    protected Component buildDescription() {
        // TODO "Contains [any] of the following: [values]
        return null;
    }
}
