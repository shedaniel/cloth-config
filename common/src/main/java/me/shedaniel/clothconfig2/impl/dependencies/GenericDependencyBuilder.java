package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.DependencyBuilder;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MatcherCondition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//FIXME can probably just extend MultiConditionDependencyBuilder?
public class GenericDependencyBuilder<T> implements DependencyBuilder<GenericDependency<T>, GenericDependencyBuilder<T>> {
    // FIXME, until a GenericCondition exists, we can only support MatcherConditions
    private final Set<Condition<T>> conditions = new HashSet<>();
    private final ConfigEntry<T> gui;
    private boolean tooltip = true;
    
    public <E extends ConfigEntry<T>> GenericDependencyBuilder(E gui) {
        this.gui = gui;
    }
    
    @Override
    public GenericDependency<T> build() {
        if (conditions.isEmpty())
            throw new IllegalArgumentException("ComparatorDependency requires at least 1 condition.");
        GenericDependency<T> dependency = new GenericDependency<>(this.gui);
        dependency.shouldGenerateTooltip(tooltip);
        dependency.addConditions(conditions);
        return dependency;
    }
    
    @Override
    public GenericDependencyBuilder<T> generateTooltip(boolean shouldGenerate) {
        this.tooltip = shouldGenerate;
        return this;
    }
    
    @SafeVarargs
    public final <E extends ConfigEntry<T>> GenericDependencyBuilder<T> matching(E... gui) {
        return matching(Set.of(gui));
    }
    
    public <E extends ConfigEntry<T>> GenericDependencyBuilder<T> matching(Collection<E> guis) {
        conditions.addAll(guis.stream().map(gui -> new MatcherCondition<T>(gui))
                .collect(Collectors.toUnmodifiableSet()));
        
        return this;
    }
    
    public GenericDependencyBuilder<T> matching(Condition<T> condition) {
        conditions.add(condition);
        return this;
    }
    
    public GenericDependencyBuilder<T> withConditions(Collection<? extends Condition<T>> conditions) {
        this.conditions.addAll(conditions);
        return this;
    }
}
