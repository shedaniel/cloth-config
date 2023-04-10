package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;

public class EnumDependency<T extends Enum<?>> extends ConfigEntryDependency<T, EnumListEntry<T>> {
    
    EnumDependency(EnumListEntry<T> entry) {
        super(entry);
    }
}
