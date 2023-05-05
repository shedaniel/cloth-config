package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class ListStaticConditionBuilder<T> extends ListConditionBuilder<T> {
    
    private final List<T> values;
    
    @SafeVarargs
    public ListStaticConditionBuilder(ContainmentRequirement requirement, T... values) {
        this(requirement, List.of(values));
    }
    public ListStaticConditionBuilder(ContainmentRequirement requirement, Collection<T> values) {
        super(requirement);
        this.values = values.stream().toList();
    }
    
    @Override
    protected Predicate<Collection<T>> buildPredicate() {
        return collection -> this.requirement.check(collection, this.values);
    }
    
    @Override
    protected Component buildDescription() {
        // TODO "Contains [any] of the following: [values]
        // [has] [any] of the following:
        // [matches] [all] of the following:
        ContainmentRequirement.Simplified simpleRequirement = this.requirement.simplified();
        Component adjective = Component.translatable("text.cloth-config.dependencies.list_req." + (simpleRequirement.condition() ? "has" : "not_has"));
        Component quantity = simpleRequirement.requirement().getText();
        return null;
    }
}
