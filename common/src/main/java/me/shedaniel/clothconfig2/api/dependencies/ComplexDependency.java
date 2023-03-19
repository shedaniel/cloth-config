package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;

/**
 * {@inheritDoc}
 *
 * Represents a dependency on a {@link AbstractConfigEntry}
 * <br><br>
 * In this implementation, the condition {@code C} is represented using a {@link Condition} object.
 */
public abstract class ComplexDependency<T, C extends Condition<T>, E extends AbstractConfigEntry<T>> extends ConfigEntryDependency<T, C, E> {
    
    public ComplexDependency(E entry) {
        super(entry);
    }
    
    /**
     * {@inheritDoc}
     *
     * <br><br>
     * This implementation checks if any condition matches the depended-on config entry's value.
     */
    @Override
    public boolean check() {
        T value = getEntry().getValue();
        return getConditions().stream().anyMatch(condition -> condition.check(value));
    }
}
