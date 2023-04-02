package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.EnumCondition;

public class EnumDependencyBuilder<T extends Enum<?>> extends ConfigEntryDependencyBuilder<T, EnumListEntry<T>, EnumDependency<T>, EnumDependencyBuilder<T>> {
    
    public EnumDependencyBuilder(EnumListEntry<T> gui) {
        super(gui);
    }
    
    @Override
    public EnumDependencyBuilder<T> matching(T condition) {
        return matching(new EnumCondition<>(condition));
    }
    
    @Override
    public EnumDependency<T> build() {
        return finishBuilding(new EnumDependency<>(this.gui));
    }
}
