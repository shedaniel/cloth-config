package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ContainmentRequirement;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import me.shedaniel.clothconfig2.gui.entries.BaseListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.ListStaticConditionBuilder;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListEntryDependencyBuilder<T> extends AbstractDependencyBuilder<Condition<Collection<T>>, ListEntryDependencyBuilder<T>> {
    
    private final BaseListEntry<T, ?, ?> gui;
    private final Set<Condition<Collection<T>>> conditions = new HashSet<>();
    
    private boolean tooltip = true;
    private GroupRequirement requirement = GroupRequirement.ANY;
    
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
        if (conditions.isEmpty())
            throw new IllegalArgumentException();
        ListEntryDependency<T> dependency = new ListEntryDependency<>(gui);
        dependency.setRequirement(requirement);
        dependency.displayTooltips(tooltip);
        dependency.addConditions(conditions);
        return dependency;
    }
    
    @Override
    public ListEntryDependencyBuilder<T> withRequirement(GroupRequirement requirement) {
        this.requirement = requirement;
        return this;
    }
    
    @Override
    public ListEntryDependencyBuilder<T> displayTooltips(boolean showTooltips) {
        this.tooltip = showTooltips;
        return this;
    }
    
    @Override
    protected Component generateDescription() {
        // TODO
        return null;
    }
    
    @Override
    protected Function<String, Component[]> generateTooltipProvider() {
        return null;
    }
}
