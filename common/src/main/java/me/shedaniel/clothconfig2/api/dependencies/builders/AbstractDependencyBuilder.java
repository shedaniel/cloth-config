package me.shedaniel.clothconfig2.api.dependencies.builders;

import me.shedaniel.clothconfig2.api.dependencies.ComplexDependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.entries.ConfigEntry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDependencyBuilder<T, E extends ConfigEntry<T>, D extends ComplexDependency<T, C, E, D>, C extends Condition<T>, SELF extends AbstractDependencyBuilder<T, E, D, C, SELF>> implements DependencyBuilder<D, SELF> {
    
    protected final E gui;
    protected final Set<C> conditions = new HashSet<>();
    private boolean hidden = false;
    
    protected AbstractDependencyBuilder(E gui) {
        this.gui = gui;
    }
    
    /**
     * Finishes building the given {@code dependency} by applying anything defined in this abstract class, for example
     * applying any conditions added using {@link #withCondition(Condition) withCondition()}.
     * <br><br>
     * Should be used by implementations of {@link #build()}.
     * 
     * @param dependency the dependency to finish building
     * @return the built dependency
     */
    protected final D finishBuilding(D dependency) {
        dependency.hiddenWhenNotMet(hidden);
        dependency.addConditions(this.conditions);
        return dependency;
    }
    
    /**
     * Generates a simple {@link Condition condition} that compares the given {@code value} against the depended-on
     * config entry's value. The condition is considered met if the two values are equal.
     * <br><br>
     * The generated condition will be added to the dependency being built.
     * 
     * @param value a condition value to be checked against the depended-on config entry 
     * @return this instance, for chaining
     */
    public abstract SELF withCondition(T value);
    
    /**
     * Add a {@link Condition condition} to the dependency being built.
     * 
     * @param condition a {@link Condition condition} to be added to the dependency being built
     * @return this instance, for chaining
     */
    public SELF withCondition(C condition) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.conditions.add(condition);
        
        return self;
    }
    
    /**
     * Add multiple {@link Condition conditions} to the dependency being built.
     * 
     * @param conditions a {@link Collection} containing {@link Condition conditions} to be added to the dependency being built
     * @return this instance, for chaining
     */
    public SELF withConditions(Collection<C> conditions) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.conditions.addAll(conditions);
        
        return self;
    }
    
    @Override
    public SELF hideWhenNotMet(boolean shouldHide) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.hidden = shouldHide;
        
        return self;
    }
}
