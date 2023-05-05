package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;
import me.shedaniel.clothconfig2.gui.entries.BaseListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.ListStaticConditionBuilder;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListEntryDependencyBuilder<T> extends AbstractDependencyBuilder<Condition<Collection<T>>, ListEntryDependencyBuilder<T>> {
    
    private final BaseListEntry<T, ?, ?> gui;
    
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
        return matching(new ListStaticConditionBuilder<>(requirement, values).build());
    }
    
    public ListEntryDependencyBuilder<T> matching(Condition<Collection<T>> condition) {
        this.conditions.add(condition);
        return this;
    }
    
    public ListEntryDependencyBuilder<T> matching(Collection<Condition<Collection<T>>> conditions) {
        this.conditions.addAll(conditions);
        return this;
    }
    
    @Override
    public Dependency build() {
        return finishBuilding(new ListEntryDependency<>(gui));
    }
    
    @Override
    protected Component generateDescription() {
        // TODO
        return null;
    }
    
    @Override
    protected Function<String, Component[]> generateTooltipProvider() {
        // TODO
        // [has] [any] of the following:
        // [matches] [all] of the following:
        return null;
    }
}
