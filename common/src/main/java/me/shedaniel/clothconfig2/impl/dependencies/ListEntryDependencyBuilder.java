package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.dependencies.FinishDependencyBuilder;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ContainmentRequirement;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MultiCondition;
import me.shedaniel.clothconfig2.gui.entries.BaseListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.CollectionStaticCondition;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListEntryDependencyBuilder<T> implements FinishDependencyBuilder<ListEntryDependency<T>, ListEntryDependencyBuilder<T>> {
    
    private final BaseListEntry<T, ?, ?> gui;
    
    private boolean tooltip = true;
    private final Set<MultiCondition<T>> conditions = new HashSet<>();
    
    public ListEntryDependencyBuilder(BaseListEntry<T, ?, ?> gui) {
        this.gui = gui;
    }
    
    @SafeVarargs
    public final ListEntryDependencyBuilder<T> matching(ContainmentRequirement requirement, T value, T... values) {
        Set<T> allValues = Streams.concat(Stream.of(value), Arrays.stream(values))
                .collect(Collectors.toUnmodifiableSet());
        
        return matching(requirement, allValues);
    }
    
    public ListEntryDependencyBuilder<T> matching(ContainmentRequirement requirement, Collection<T> values) {
        this.conditions.add(new CollectionStaticCondition<>(requirement, values));
        return this;
    }
    
    public ListEntryDependencyBuilder<T> matching(MultiCondition<T> condition) {
        this.conditions.add(condition);
        return this;
    }
    
    public ListEntryDependencyBuilder<T> matching(Collection<MultiCondition<T>> conditions) {
        this.conditions.addAll(conditions);
        return this;
    }
    
    @Override
    public ListEntryDependency<T> build() {
        if (conditions.isEmpty())
            throw new IllegalArgumentException();
        ListEntryDependency<T> dependency = new ListEntryDependency<>(gui);
        dependency.shouldGenerateTooltip(tooltip);
        dependency.addConditions(conditions);
        return dependency;
    }
    
    @Override
    public ListEntryDependencyBuilder<T> generateTooltip(boolean shouldGenerate) {
        this.tooltip = shouldGenerate;
        return this;
    }
}
