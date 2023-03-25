package me.shedaniel.clothconfig2.api.dependencies.builders;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.ConfigEntryDependency;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @param <T> the type the dependency deals with
 * @param <E> the {@link ConfigEntry} type depended-on
 * @param <C> the {@link Condition} type the dependency uses
 * @param <D> the {@link Dependency} type that will be built
 * @param <SELF> the type to be returned by chainable methods
 */
public abstract class MultiConditionDependencyBuilder<T, E extends ConfigEntry<T>, C extends Condition<T>, D extends ConfigEntryDependency<T, E, C>, SELF extends MultiConditionDependencyBuilder<T, E, C, D, SELF>> extends AbstractDependencyBuilder<T, E, C, D, SELF> {
    
    protected final Set<C> conditions = new HashSet<>();
    private final int minConditions;
    
    protected MultiConditionDependencyBuilder(E gui, int requiredConditions) {
        super(gui);
        this.minConditions = requiredConditions;
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
    protected D finishBuilding(D dependency) {
        if (conditions.size() < this.minConditions)
            throw new IllegalArgumentException("%s requires at least %d condition%s.".formatted(dependency.getClass().getSimpleName(), this.minConditions, this.minConditions == 1 ? "" : "s"));
        dependency.addConditions(this.conditions);
        return super.finishBuilding(dependency);
    }
    
    @Override
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
}
