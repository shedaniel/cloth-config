package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.Dependency;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.network.chat.Component;

public class BooleanDependency extends Dependency<Boolean, BooleanListEntry> {
    
    private BooleanDependency(BooleanListEntry entry, Boolean condition) {
        super(entry);
        this.setCondition(condition);
    }
    
    public static BooleanDependency hiddenWhenNotSatisfied(BooleanListEntry entry) {
        return hiddenWhenNotSatisfied(entry, true);
    }
    
    public static BooleanDependency hiddenWhenNotSatisfied(BooleanListEntry entry, boolean condition) {
        BooleanDependency dependency = new BooleanDependency(entry, condition);
        dependency.setHiddenWhenDisabled(true);
        return dependency;
    }
    
    public static BooleanDependency disabledWhenNotSatisfied(BooleanListEntry entry) {
        return disabledWhenNotSatisfied(entry, true);
    }
    
    public static BooleanDependency disabledWhenNotSatisfied(BooleanListEntry entry, boolean condition) {
        return new BooleanDependency(entry, condition);
    }
    
    @Override
    protected Component getConditionText(Boolean condition) {
        return getEntry().getYesNoText(condition);
    }
}
