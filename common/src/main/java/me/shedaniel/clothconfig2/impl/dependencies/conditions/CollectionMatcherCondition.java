package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MultiCondition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;

import java.util.Collection;

public class CollectionMatcherCondition<T> extends AbstractMatcherCondition<Collection<T>> implements MultiCondition<T> {
    
    private final ContainmentRequirement requirement;
    
    public CollectionMatcherCondition(ContainmentRequirement requirement, ConfigEntry<Collection<T>> gui) {
        this(requirement, gui, false);
    }
    
    public CollectionMatcherCondition(ContainmentRequirement requirement, ConfigEntry<Collection<T>> gui, boolean inverted) {
        super(gui, inverted);
        this.requirement = requirement;
    }
    
    @Override
    public ContainmentRequirement getRequirement() {
        return this.requirement;
    }
}
