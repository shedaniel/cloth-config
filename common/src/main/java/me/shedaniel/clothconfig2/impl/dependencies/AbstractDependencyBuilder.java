package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.DependencyBuilder;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MatcherCondition;
import org.jetbrains.annotations.Contract;

/**
 * @param <T> the type the dependency deals with
 * @param <E> the {@link ConfigEntry} type depended-on
 * @param <D> the {@link Dependency} type that will be built
 * @param <SELF> the type to be returned by chainable methods
 */
public abstract class AbstractDependencyBuilder<T, E extends ConfigEntry<T>, D extends ConfigEntryDependency<T, E>, SELF extends AbstractDependencyBuilder<T, E, D, SELF>> implements DependencyBuilder<D, SELF> {
    
    protected final E gui;
    private boolean tooltip = true;
    
    protected AbstractDependencyBuilder(E gui) {
        this.gui = gui;
    }
    
    /**
     * Finishes building the given {@link Dependency dependency} by applying anything defined in this abstract class.
     * <br><br>
     * Should be used by implementations of {@link #build()}.
     * 
     * @param dependency the dependency to finish building
     * @return the finished dependency
     */
    @Contract(value = "_ -> param1", mutates = "param1")
    protected D finishBuilding(D dependency) {
        dependency.shouldGenerateTooltip(tooltip);
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
    public abstract SELF matching(T value);
    
    
    /**
     * Generates a simple {@link MatcherCondition} that compares the given {@code gui}'s value against the depended-on
     * config entry's value.
     * <br><br>
     * The generated condition will be added to the dependency being built.
     *
     * @param gui a {@link Condition condition} to be added to the dependency being built
     * @return this instance, for chaining
     */
    public SELF matching(ConfigEntry<T> gui) {
        return matching(new MatcherCondition<>(gui));
    }
    
    /**
     * Add a {@link Condition condition} to the dependency being built.
     * 
     * @param condition a {@link Condition condition} to be added to the dependency being built
     * @return this instance, for chaining
     */
    public abstract SELF matching(Condition<T> condition);
    
    @Override
    public SELF generateTooltip(boolean shouldGenerate) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.tooltip = shouldGenerate;
        return self;
    }
}
