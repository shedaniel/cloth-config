package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.FinishDependencyBuilder;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.GenericMatcherCondition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @param <T> the type the dependency deals with
 * @param <E> the {@link ConfigEntry} type depended-on
 * @param <D> the {@link Dependency} type that will be built
 * @param <SELF> the type to be returned by chainable methods
 */
public abstract class ConfigEntryDependencyBuilder<T, E extends ConfigEntry<T>, D extends ConfigEntryDependency<T, E>, SELF extends ConfigEntryDependencyBuilder<T, E, D, SELF>> implements FinishDependencyBuilder<D, SELF> {
    
    private static final int minConditions = 1;
    
    protected final E gui;
    protected final Set<Condition<T>> conditions = new HashSet<>();
    
    protected GroupRequirement requirement = GroupRequirement.ANY;
    protected boolean tooltip = true;
    
    protected ConfigEntryDependencyBuilder(E gui) {
        this.gui = gui;
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
    public abstract SELF matching(T value);
    
    /**
     * Generates a simple {@link GenericMatcherCondition} that compares the given {@code gui}'s value against the depended-on
     * config entry's value.
     * <br><br>
     * The generated condition will be added to the dependency being built.
     *
     * @param gui the gui whose value should be compared with the depended-on gui's
     * @return this instance, for chaining
     */
    public SELF matching(ConfigEntry<T> gui) {
        return matching(new GenericMatcherCondition<>(gui));
    }
    
    /**
     * Finishes building the given {@code dependency} by applying anything defined in this abstract class, for example
     * applying any conditions added using {@link #matching(Condition)}.
     * <br><br>
     * Should be used by implementations of {@link #build()}.
     * 
     * @param dependency the dependency to finish building
     * @return the built dependency
     */
    protected D finishBuilding(D dependency) {
        if (conditions.size() < minConditions)
            throw new IllegalArgumentException("%s requires at least %d condition%s.".formatted(dependency.getClass().getSimpleName(), minConditions, minConditions == 1 ? "" : "s"));
        dependency.addConditions(this.conditions);
        dependency.displayTooltips(tooltip);
        dependency.setRequirement(requirement);
        return dependency;
    }
    
    @Override
    public SELF withRequirement(GroupRequirement requirement) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.requirement = requirement;
        return self;
    }
    
    public SELF matching(Condition<T> condition) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.conditions.add(condition);
        
        return self;
    }
    
    @Override
    public SELF generateTooltip(boolean shouldGenerate) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.tooltip = shouldGenerate;
        return self;
    }
    
    /**
     * Add multiple {@link Condition conditions} to the dependency being built.
     * 
     * @param conditions a {@link Collection} containing {@link Condition conditions} to be added to the dependency being built
     * @return this instance, for chaining
     */
    public SELF matching(Collection<? extends Condition<T>> conditions) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.conditions.addAll(conditions);
        
        return self;
    }
}
