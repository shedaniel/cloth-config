package me.shedaniel.clothconfig2.api.dependencies.builders;

import me.shedaniel.clothconfig2.api.dependencies.BooleanDependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;

import java.util.Collection;

public class BooleanDependencyBuilder extends AbstractDependencyBuilder<Boolean, BooleanListEntry, BooleanDependency, BooleanCondition, BooleanDependencyBuilder> {
    
    public BooleanDependencyBuilder(BooleanListEntry gui) {
        super(gui);
    }
    
    @Override
    public BooleanDependencyBuilder withCondition(Boolean value) {
        return withCondition(new BooleanCondition(value));
    }
    
    @Override
    public BooleanDependencyBuilder withCondition(BooleanCondition condition) {
        if (!this.conditions.isEmpty())
            throw new IllegalArgumentException("BooleanDependency does not support multiple conditions");
        return super.withCondition(condition);
    }
    
    /**
     * @deprecated {@link BooleanDependency} does not support multiple conditions, use {@link #withCondition(BooleanCondition)}
     *             instead
     */
    @Override
    @Deprecated
    public BooleanDependencyBuilder withConditions(Collection<BooleanCondition> conditions) {
        if (!this.conditions.isEmpty() || conditions.size() > 1)
            throw new IllegalArgumentException("BooleanDependency does not support multiple conditions");
        return super.withConditions(conditions);
    }
    
    @Override
    public BooleanDependency build() {
        // Default condition is "true"
        if (conditions.isEmpty())
            conditions.add(new BooleanCondition(true));
        // Ensure we haven't got multiple conditions
        else if (conditions.size() > 1)
            throw new IllegalStateException("BooleanDependency does not support multiple conditions");

        return finishBuilding(new BooleanDependency(this.gui));
    }
}
