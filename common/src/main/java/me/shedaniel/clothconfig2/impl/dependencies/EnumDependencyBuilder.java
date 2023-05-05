package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.StaticConditionBuilder;

public class EnumDependencyBuilder<T extends Enum<?>> extends ConfigEntryDependencyBuilder<T, EnumDependencyBuilder<T>> {
    
    public EnumDependencyBuilder(EnumListEntry<T> gui) {
        super(gui);
    }
    
    @Override
    public EnumDependencyBuilder<T> matching(T value) {
        return matching(new StaticConditionBuilder<>(value).describeUsing(gui).build());
    }
}
