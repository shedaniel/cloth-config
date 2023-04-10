package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;

/**
 * Represents a dependency on a {@link ConfigEntry}
 *
 * @param <T> the type this dependency deals with
 * @param <E> the {@link ConfigEntry} type
 */
public abstract class ConfigEntryDependency<T, E extends ConfigEntry<T>> extends AbstractElementDependency<Condition<T>, E> {
    
    protected ConfigEntryDependency(E entry) {
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
        T value = getElement().getValue();
        return this.getRequirement().matches(getConditions(), condition -> condition.check(value));
    }
}
