package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.network.chat.Component;

public class SelectionDependency<T> extends ConfigEntryDependency<T, SelectionListEntry<T>> {
    
    @SafeVarargs //FIXME is this actually safe from heap pollution? T... aka Object[] seems okay-ish?
    SelectionDependency(SelectionListEntry<T> entry, T condition, T... conditions) {
        super(entry);
        setCondition(condition);
        if (conditions.length > 0)
            addCondition(conditions);
    }
    
    @Override
    protected Component getConditionText(T condition) {
        return getEntry().getTextFor(condition);
    }
}
