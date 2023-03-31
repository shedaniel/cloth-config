package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents an abstract dependency.
 * 
 * @param <C> The type used for the condition
 * @param <E> The depended-on element type
 */
public abstract class AbstractDependency<C, E> implements Dependency {
    private final E element;
    
    private final Set<C> conditions = new LinkedHashSet<>();
    private boolean generateTooltips = true;
    
    protected AbstractDependency(E element) {this.element = element;}
    
    /**
     * @return the element that is depended on
     */
    public final E getElement() {
        return element;
    }
    
    /**
     * Get the dependency's conditions.
     *
     * @return a {@link Set} containing the dependency's conditions
     */
    public final Set<C> getConditions() {
        return conditions;
    }
    
    /**
     * Adds one or more conditions to the dependency. If any condition matches the entry's value,
     * then the dependency is met.
     *
     * @param conditions a {@link Collection} of conditions to be added
     */
    public final void addConditions(Collection<? extends C> conditions) {
        this.conditions.addAll(conditions.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet()));
    }
    
    public void shouldGenerateTooltip(boolean shouldGenerate) {
        this.generateTooltips = shouldGenerate;
    }
    
    @Override
    public boolean hasTooltip() {
        return this.generateTooltips;
    }
}
