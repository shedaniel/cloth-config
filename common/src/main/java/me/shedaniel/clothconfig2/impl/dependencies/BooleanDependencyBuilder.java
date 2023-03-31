package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConfigEntryMatcher;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import org.jetbrains.annotations.Nullable;

public class BooleanDependencyBuilder extends MultiConditionDependencyBuilder<Boolean, BooleanListEntry, BooleanDependency, BooleanDependencyBuilder> {
    
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
            return super.matching(matcher);
        }
        
        if (this.condition != null)
            throw new IllegalArgumentException("BooleanDependency does not support multiple conditions");
        
        this.condition = condition;
        
        return this;
    }
    
    @Override
    public BooleanDependency build() {
        // Default condition is "true"
        if (condition == null && conditions.isEmpty()) {
            condition = new BooleanCondition(true);
        }
        
        // Add the static condition to the conditions list
        if (condition != null)
            super.matching(condition);
    
        return finishBuilding(new BooleanDependency(this.gui));
    }
}
