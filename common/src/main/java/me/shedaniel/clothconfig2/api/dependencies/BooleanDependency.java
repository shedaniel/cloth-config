package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.network.chat.Component;

public class BooleanDependency extends ComplexDependency<Boolean, BooleanCondition, BooleanListEntry, BooleanDependency> {
    
    private boolean useActualText;
    
    BooleanDependency(BooleanListEntry entry, BooleanCondition condition) {
        super(entry);
        setCondition(condition);
    }
    
    @Override
    public BooleanDependency withSimpleCondition(Boolean value) {
        setCondition(new BooleanCondition(value));
        return this;
    }
    
    @Override
    protected Component getConditionText(BooleanCondition condition) {
        if (this.useActualText)
            return Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                        Component.translatable("text.cloth-config.quoted",
                                getEntry().getYesNoText(condition.getValue())));
        
        return super.getConditionText(condition);
    }
    
    @Override
    public Component getShortDescription() {
        Component condition = this.getConditions().stream()
                .map(Condition::getText)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("BooleanDependency requires exactly one condition"));
        return Component.translatable("text.cloth-config.short_description.single", getEntry().getFieldName(), condition);
    }
    
    public void useActualText(boolean shouldUseActualText) {
        this.useActualText = shouldUseActualText;
    }
}
