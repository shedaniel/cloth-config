package me.shedaniel.clothconfig2.api.dependencies.builders;

import me.shedaniel.clothconfig2.api.dependencies.EnumDependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;

public class EnumDependencyBuilder<T extends Enum<?>> extends MultiConditionDependencyBuilder<T, EnumListEntry<T>, EnumCondition<T>, EnumDependency<T>, EnumDependencyBuilder<T>> {
    
    public EnumDependencyBuilder(EnumListEntry<T> gui) {
        super(gui, 1);
    }
    
    @Override
    public EnumDependencyBuilder<T> withCondition(T condition) {
        return withCondition(new EnumCondition<>(condition));
    }
    
    @Override
    public EnumDependency<T> build() {
        if (conditions.isEmpty())
            throw new IllegalStateException("EnumDependency requires at least one condition");
        
        return finishBuilding(new EnumDependency<>(this.gui));
    }
}
