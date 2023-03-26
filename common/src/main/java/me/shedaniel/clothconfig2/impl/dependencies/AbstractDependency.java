package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Represents an abstract dependency.
 * 
 * @param <C> The type used for the condition
 * @param <E> The depended-on element type
 */
public abstract class AbstractDependency<C, E> implements Dependency {
    private final E element;
    
    private final Collection<C> conditions = new ArrayList<>();
    
    private boolean shouldHide = false;
    
    protected AbstractDependency(E element) {this.element = element;}
    
    /**
     * @return the element that is depended on
     */
    public final E getElement() {
        return element;
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
            if (!this.element.equals(dependency.element))
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
        return Boolean.hashCode(this.shouldHide) + 8 * this.element.hashCode() + 16 * this.conditions.hashCode();
    }
}