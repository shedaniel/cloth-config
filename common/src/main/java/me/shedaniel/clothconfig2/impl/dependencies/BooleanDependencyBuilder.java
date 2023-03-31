package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConfigEntryMatcher;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BooleanDependencyBuilder extends AbstractDependencyBuilder<Boolean, BooleanListEntry, BooleanDependency, BooleanDependencyBuilder> {
    
    private final Set<ConfigEntryMatcher<Boolean>> matchers = new HashSet<>();
    
    private @Nullable Condition<Boolean> condition = null;
    
    public BooleanDependencyBuilder(BooleanListEntry gui) {
        super(gui);
    }
    
    @Override
    public BooleanDependencyBuilder matching(Boolean value) {
        return matching(new BooleanCondition(value));
    }
    
    @Override
    public BooleanDependencyBuilder matching(Condition<Boolean> condition) {
        if (condition instanceof ConfigEntryMatcher<Boolean> matcher) {
            matchers.add(matcher);
            return this;
        }
        
        if (this.condition != null)
            throw new IllegalArgumentException("BooleanDependency does not support multiple conditions");
        
        this.condition = condition;
        
        return this;
    }
    
    @Override
    public BooleanDependency build() {
        // Default condition is "true"
        if (condition == null && matchers.isEmpty())
            condition = new BooleanCondition(true);
    
        BooleanDependency dependency = new BooleanDependency(this.gui);
        dependency.addConditions(matchers);
        if (condition != null)
            dependency.addConditions(Collections.singletonList(condition));
    
        return finishBuilding(dependency);
    }
}
