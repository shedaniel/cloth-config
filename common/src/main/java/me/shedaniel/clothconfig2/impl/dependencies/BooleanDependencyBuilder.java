package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConfigEntryMatcher;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BooleanDependencyBuilder extends AbstractDependencyBuilder<Boolean, BooleanListEntry, BooleanCondition, BooleanDependency, BooleanDependencyBuilder> {
    
    private BooleanCondition condition = null;
    private Set<ConfigEntryMatcher<Boolean>> comparators = new HashSet<>();
    
    public BooleanDependencyBuilder(BooleanListEntry gui) {
        super(gui);
    }
    
    @Override
    public BooleanDependencyBuilder withCondition(Boolean value) {
        return withCondition(new BooleanCondition(value));
    }
    
    @Override
    public BooleanDependencyBuilder withCondition(BooleanCondition condition) {
        if (this.condition != null)
            throw new IllegalArgumentException("BooleanDependency does not support multiple conditions");
        this.condition = condition;
        
        return this;
    }
    
    @Override
    public BooleanDependencyBuilder matching(Collection<ConfigEntryMatcher<Boolean>> comparators) {
        this.comparators.addAll(comparators);
        return this;
    }
    
    @Override
    public BooleanDependencyBuilder matching(ConfigEntryMatcher<Boolean> comparator) {
        comparators.add(comparator);
        return this;
    }
    
    @Override
    public BooleanDependency build() {
        // Default condition is "true"
        if (condition == null && comparators.isEmpty())
            condition = new BooleanCondition(true);
    
        BooleanDependency dependency = new BooleanDependency(this.gui);
        dependency.addConditions(comparators);
        if (condition != null)
            dependency.addConditions(Collections.singletonList(condition));
    
        return finishBuilding(dependency);
    }
}
