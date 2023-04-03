package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.AbstractStaticCondition;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.BooleanStaticCondition;

import java.util.Collection;

public class BooleanDependencyBuilder extends ConfigEntryDependencyBuilder<Boolean, BooleanListEntry, BooleanDependency, BooleanDependencyBuilder> {
    
    private boolean hasStaticCondition = false;
    
    public BooleanDependencyBuilder(BooleanListEntry gui) {
        super(gui);
    }
    
    @Override
    public BooleanDependencyBuilder matching(Boolean value) {
        return matching(new BooleanStaticCondition(value));
    }
    
    /**
     * {@inheritDoc}
     * <br><br>
     * This implementation will throw an {@link IllegalArgumentException} if it is used to add more than one
     * {@link AbstractStaticCondition static conditions} to the dependency.
     * 
     * @param condition a {@link Condition condition} to be added to the dependency being built 
     * @return this instance, for chaining
     */
    @Override
    public BooleanDependencyBuilder matching(Condition<Boolean> condition) {
        if (condition instanceof AbstractStaticCondition<Boolean>) {
            if (hasStaticCondition)
                throw new IllegalArgumentException("BooleanDependency does not support multiple static conditions");
            hasStaticCondition = true;
        }
        
        return super.matching(condition);
    }
    
    /**
     * {@inheritDoc}
     * <br><br>
     * This implementation will throw an {@link IllegalArgumentException} if it is used to add more than one
     * {@link AbstractStaticCondition static conditions} to the dependency.
     * 
     * @param conditions a {@link Collection} containing {@link Condition conditions} to be added to the dependency being built 
     * @return this instance, for chaining
     */
    @Override
    public BooleanDependencyBuilder matching(Collection<? extends Condition<Boolean>> conditions) {
        long count = conditions.stream().filter(AbstractStaticCondition.class::isInstance).count();
        if (count > 0) {
            if (hasStaticCondition || count > 1)
                throw new IllegalArgumentException("BooleanDependency does not support multiple static conditions");
            hasStaticCondition = true;
        }
        return super.matching(conditions);
    }
    
    @Override
    public BooleanDependency build() {
        // Default condition is "true"
        if (!hasStaticCondition && conditions.isEmpty())
            this.matching(true);
    
        return finishBuilding(new BooleanDependency(this.gui));
    }
}
