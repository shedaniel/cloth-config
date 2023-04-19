package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;

import java.util.Collection;

public abstract class ListConditionBuilder<T> extends SimpleConditionBuilder<Collection<T>, ListConditionBuilder<T>> {
    
    protected final ContainmentRequirement requirement;
    
    public ListConditionBuilder(ContainmentRequirement requirement) {
        this.requirement = requirement;
    }
}
