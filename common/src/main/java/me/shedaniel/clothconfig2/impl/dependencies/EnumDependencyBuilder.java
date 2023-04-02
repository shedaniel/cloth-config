package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;

public class EnumDependencyBuilder<T extends Enum<?>> extends AbstractDependencyBuilder<T, EnumListEntry<T>, EnumDependency<T>, EnumDependencyBuilder<T>> {
    
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
