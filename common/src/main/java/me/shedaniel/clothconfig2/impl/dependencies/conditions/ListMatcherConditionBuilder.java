package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class ListMatcherConditionBuilder<T> extends ListConditionBuilder<T> {
    
    
    
    private final ConfigEntry<List<T>> otherGui;
    
    public ListMatcherConditionBuilder(ContainmentRequirement requirement, ConfigEntry<List<T>> gui) {
        super(requirement);
        this.otherGui = gui;
    }
    
    @Override
    protected Predicate<Collection<T>> buildPredicate() {
        return collection -> this.requirement.check(collection, this.otherGui.getValue());
    }
    
    @Override
    protected Component buildDescription() {
        // TODO "Contains [any] of [otherGui]'s values"
        return null;
    }
}
