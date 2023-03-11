package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.Dependency;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.network.chat.Component;

public class BooleanDependency extends Dependency<Boolean, BooleanListEntry> {
    
    public BooleanDependency(BooleanListEntry entry, Boolean value) {
        super(entry, value);
    }
    
    public BooleanDependency(BooleanListEntry entry) {
        super(entry, true);
    }
    
    @Override
    protected Component getValueText(Boolean value) {
        return getEntry().getYesNoText(value);
    }
}
