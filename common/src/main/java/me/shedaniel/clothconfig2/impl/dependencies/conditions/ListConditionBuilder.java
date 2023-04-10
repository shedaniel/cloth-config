package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;

import java.util.List;

public abstract class ListConditionBuilder<T> extends SimpleConditionBuilder<List<T>, ListConditionBuilder<T>> {
    
    protected final ContainmentRequirement requirement;
    
    public ListConditionBuilder(ContainmentRequirement requirement) {
        this.requirement = requirement;
    }
}
