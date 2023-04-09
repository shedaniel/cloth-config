package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.MultiCondition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionStaticCondition<T> extends AbstractStaticCondition<Collection<T>> implements MultiCondition<T> {
    
    private final ContainmentRequirement requirement;
    
    public CollectionStaticCondition(ContainmentRequirement requirement, T value) {
        this(requirement, Collections.singleton(value));
    }
    
    public CollectionStaticCondition(ContainmentRequirement requirement, Collection<T> values) {
        this(requirement, values.stream().collect(Collectors.toUnmodifiableSet()));
    }
    
    public CollectionStaticCondition(ContainmentRequirement requirement, Set<T> values) {
        this(requirement, values, false);
    }
    
    public CollectionStaticCondition(ContainmentRequirement requirement, Set<T> values, boolean inverted) {
        super(values, inverted);
        this.requirement = requirement;
    }
    
    @Override
    public ContainmentRequirement getRequirement() {
        return this.requirement;
    }
    
    @Override
    public Component getText(boolean inverted) {
        //TODO
        return null;
    }
}
