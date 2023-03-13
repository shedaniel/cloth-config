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
    
    @Override
    public Component getShortDescription() {
        Component condition = getConditionText(this.getConditions().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("BooleanDependency requires exactly one condition")));
        return Component.translatable("text.cloth-config.boolean_dependency.short_description", getEntry().getFieldName(), condition);
    }
}
