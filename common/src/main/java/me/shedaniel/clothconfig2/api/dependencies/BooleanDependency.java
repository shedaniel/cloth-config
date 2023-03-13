package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.network.chat.Component;

public class BooleanDependency extends ConfigEntryDependency<Boolean, BooleanListEntry> {
    
    BooleanDependency(BooleanListEntry entry, Boolean condition) {
        super(entry);
        this.setCondition(condition);
    }
    
    @Override
    protected Component getConditionText(Boolean condition) {
        return getEntry().getYesNoText(condition);
    }
}
