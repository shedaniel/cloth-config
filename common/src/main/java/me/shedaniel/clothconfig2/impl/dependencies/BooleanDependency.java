package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.StaticCondition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.network.chat.Component;

public class BooleanDependency extends ConfigEntryDependency<Boolean, BooleanListEntry> {
    
    private boolean useActualText;
    
    BooleanDependency(BooleanListEntry entry) {
        super(entry);
    }
    
    @Override
    protected Component getStaticConditionText(StaticCondition<Boolean> condition, boolean inverted) {
        if (this.useActualText)
            return Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                        Component.translatable("text.cloth-config.quoted",
                                getElement().getYesNoText(condition.inverted() != condition.getValue())));
        
        return super.getStaticConditionText(condition, inverted);
    }
    
    @Override
    public Component getShortDescription(boolean inverted) {
        Component conditionText = this.getConditions().stream()
                .map(condition -> getConditionText(condition, inverted))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("BooleanDependency requires exactly one condition"));
        return Component.translatable("text.cloth-config.dependencies.short_description.single", getElement().getFieldName(), conditionText);
    }
    
    public void useActualText(boolean shouldUseActualText) {
        this.useActualText = shouldUseActualText;
    }
}
