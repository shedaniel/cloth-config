package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.network.chat.Component;

public class SelectionDependency<T extends Enum<?>> extends ComplexDependency<T, EnumCondition<T>, EnumListEntry<T>, SelectionDependency<T>> {
    
    SelectionDependency(EnumListEntry<T> entry, EnumCondition<T> condition) {
        super(entry);
        setCondition(condition);
    }
    
    @Override
    public Component getShortDescription() {
        int conditions = getConditions().size();
        
        if (conditions == 1) {
            Component condition = (this.getConditions().stream()
                    .map(Condition::getValue)
                    .map(c -> getEntry().getTextFor(c))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Expected exactly one condition")));
            return Component.translatable("text.cloth-config.selection_dependency.short_description.one", getEntry().getFieldName(), condition);
        }
        
        return Component.translatable("text.cloth-config.selection_dependency.short_description.many", getEntry().getFieldName(), conditions);
    }
    
    @Override
    public SelectionDependency<T> withSimpleCondition(T value) {
        addCondition(new EnumCondition<>(value));
        return this;
    }
}
