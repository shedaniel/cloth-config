package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.network.chat.Component;

public class SelectionDependency<T> extends SimpleDependency<T, SelectionListEntry<T>> {
    
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
    
    @Override
    public Component getShortDescription() {
        int conditions = getConditions().size();
        
        if (conditions == 1) {
            Component condition = getConditionText(this.getConditions().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Expected exactly one condition")));
            return Component.translatable("text.cloth-config.selection_dependency.short_description.one", getEntry().getFieldName(), condition);
        }
        
        return Component.translatable("text.cloth-config.selection_dependency.short_description.many", getEntry().getFieldName(), conditions);
    }
}
