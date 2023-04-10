package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;

public class BooleanDependency extends ConfigEntryDependency<Boolean, BooleanListEntry> {
    
    private boolean useActualText;
    
    BooleanDependency(BooleanListEntry entry) {
        super(entry);
    }
}
