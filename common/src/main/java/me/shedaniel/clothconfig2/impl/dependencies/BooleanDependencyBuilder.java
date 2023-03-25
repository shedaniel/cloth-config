package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;

import java.util.Collections;

public class BooleanDependencyBuilder extends AbstractDependencyBuilder<Boolean, BooleanListEntry, BooleanCondition, BooleanDependency, BooleanDependencyBuilder> {
    
    
    private BooleanCondition condition = null;
    
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
    public BooleanDependency build() {
        // Default condition is "true"
        if (condition == null)
            condition = new BooleanCondition(true);
    
        BooleanDependency dependency = new BooleanDependency(this.gui);
        dependency.addConditions(Collections.singletonList(condition));
    
        return finishBuilding(dependency);
    }
}
