package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ListConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;

import java.util.Collection;
import java.util.List;

/**
 * Represents a dependency on a {@link ListConfigEntry}
 * @param <T> 
 */
public class ListEntryDependency<T> extends AbstractElementDependency<Condition<Collection<T>>, ListConfigEntry<T>> {
    
    ListEntryDependency(ListConfigEntry<T> entry) {
        super(entry);
    }
    
    @Override
    public boolean check() {
        List<T> values = getElement().getValue();
        return this.getRequirement().matches(getConditions(), condition -> condition.check(values));
    }
}
