package me.shedaniel.clothconfig2.api.dependencies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents an abstract dependency.
 * 
 * @param <C> The type used for the condition
 * @param <E> the depended on type
 */
public abstract class AbstractDependency<C, E> implements Dependency {
    private final E entry;
    
    private final Collection<C> conditions = new ArrayList<>();
    
    private boolean shouldHide = false;
    
    protected AbstractDependency(E entry) {this.entry = entry;}
    
    /**
     * @return the element that is depended on
     */
    public final E getEntry() {
        return entry;
    }
    
    @Override
    public final boolean hiddenWhenNotMet() {
        return shouldHide;
    }
    
    @Override
    public final void hiddenWhenNotMet(boolean shouldHide) {
        this.shouldHide = shouldHide;
    }
    
    /**
     * Get the dependency's conditions.
     *
     * @return a {@link Collection} containing the dependency's conditions
     */
    public final Collection<C> getConditions() {
        return conditions;
    }
    
    /**
     * Adds one or more conditions to the dependency. If any condition matches the entry's value,
     * then the dependency is met.
     * <br>
     * Unlike {@code setCondition()}, existing conditions are not removed.
     *
     * @param conditions the conditions to be added
     */
    @SafeVarargs
    public final void addCondition(C... conditions) {
        addConditions(Arrays.asList(conditions));
    }
    
    /**
     * Adds one or more conditions to the dependency. If any condition matches the entry's value,
     * then the dependency is met.
     * <br>
     * Unlike {@code setCondition()}, existing conditions are not removed.
     *
     * @param conditions a {@link Collection} of conditions to be added
     */
    public final void addConditions(Collection<C> conditions) {
        this.conditions.addAll(conditions);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (obj instanceof AbstractDependency<?,?> dependency) {
            if (this.shouldHide != dependency.shouldHide)
                return false;
            if (!this.entry.equals(dependency.entry))
                return false;
            if (this.conditions.size() != dependency.conditions.size())
                return false;
            // True if all conditions have an equivalent
            return this.conditions.stream().allMatch(condition ->
                    dependency.conditions.stream().anyMatch(condition::equals));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Boolean.hashCode(this.shouldHide) + 8*this.entry.hashCode() + 16*this.conditions.hashCode();
    }
}
