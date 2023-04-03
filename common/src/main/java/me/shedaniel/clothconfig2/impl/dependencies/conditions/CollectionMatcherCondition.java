package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ContainmentRequirement;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MultiCondition;

import java.util.Collection;

public class CollectionMatcherCondition<T> extends AbstractMatcherCondition<Collection<T>> implements MultiCondition<T> {
    
    private final ContainmentRequirement requirement;
    
    protected CollectionMatcherCondition(ContainmentRequirement requirement, ConfigEntry<Collection<T>> gui) {
        super(gui);
        this.requirement = requirement;
    }
    
    @Override
    public ContainmentRequirement getRequirement() {
        return this.requirement;
    }
}
