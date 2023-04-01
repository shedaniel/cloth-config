package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.DependencyBuilder;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MatcherCondition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ComparatorDependencyBuilder<T> implements DependencyBuilder<ComparatorDependency<T>, ComparatorDependencyBuilder<T>> {
    private final Set<MatcherCondition<T>> conditions = new HashSet<>();
    private final ConfigEntry<T> gui;
    private boolean tooltip = true;
    
    public <E extends ConfigEntry<T>> ComparatorDependencyBuilder(E gui) {
        this.gui = gui;
    }
    
    @Override
    public ComparatorDependency<T> build() {
        if (conditions.isEmpty())
            throw new IllegalArgumentException("ComparatorDependency requires at least 1 condition.");
        ComparatorDependency<T> dependency = new ComparatorDependency<>(this.gui);
        dependency.shouldGenerateTooltip(tooltip);
        dependency.addConditions(conditions);
        return dependency;
    }
    
    @Override
    public ComparatorDependencyBuilder<T> generateTooltip(boolean shouldGenerate) {
        this.tooltip = shouldGenerate;
        return this;
    }
    
    public <E extends ConfigEntry<T>> ComparatorDependencyBuilder<T> matching(E... gui) {
        return matching(Set.of(gui));
    }
    
    public <E extends ConfigEntry<T>> ComparatorDependencyBuilder<T> matching(Collection<E> guis) {
        conditions.addAll(guis.stream().map(gui -> new MatcherCondition<T>(gui))
                .collect(Collectors.toUnmodifiableSet()));
        
        return this;
    }
    
    public ComparatorDependencyBuilder<T> matching(MatcherCondition<T> condition) {
        conditions.add(condition);
        return this;
    }
}
