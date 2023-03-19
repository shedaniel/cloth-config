package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;

/**
 * {@inheritDoc}
 *
 * Represents a dependency on a {@link AbstractConfigEntry}
 * <br><br>
 * In this implementation, the condition is also of type {@code T}.
 */
public abstract class SimpleDependency<T, E extends AbstractConfigEntry<T>> extends ConfigEntryDependency<T, T, E> {
    
    protected SimpleDependency(E entry) {
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
        return getConditions().stream().anyMatch(value::equals);
    }
}
