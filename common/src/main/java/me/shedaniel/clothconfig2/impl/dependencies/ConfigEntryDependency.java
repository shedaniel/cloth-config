package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;

/**
 * Represents a dependency on a {@link ConfigEntry}
 *
 * @param <T> the type this dependency deals with
 */
public class ConfigEntryDependency<T> extends AbstractElementDependency<Condition<T>, ConfigEntry<T>> {
    
    protected ConfigEntryDependency(ConfigEntry<T> entry) {
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
