package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * {@inheritDoc}
 *
 * Represents a dependency on a {@link AbstractConfigEntry}
 * <br><br>
 * In this implementation, the condition type and {@code T} are the same.
 */
public abstract class PlainDependency<T, E extends AbstractConfigEntry<T>> extends AbstractDependency<T, E, PlainDependency<T, E>> {
    
    protected PlainDependency(E entry) {
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
    
    @Override
    public Optional<Component[]> getTooltip() {
        //TODO
        return Optional.empty();
    }
}
