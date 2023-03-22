package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

public class BooleanDependency extends ComplexDependency<Boolean, BooleanCondition, BooleanListEntry> {
    
    private boolean useActualText;
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanDependency(BooleanListEntry entry) {
        super(entry);
    }
    
    @Override
    protected Component getConditionText(BooleanCondition condition) {
        if (this.useActualText)
            return Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                        Component.translatable("text.cloth-config.quoted",
                                getEntry().getYesNoText(condition.inverted() != condition.getValue())));
        
        return super.getConditionText(condition);
    }
    
    @Override
    public Component getShortDescription() {
        Component condition = this.getConditions().stream()
                .map(this::getConditionText)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("BooleanDependency requires exactly one condition"));
        return Component.translatable("text.cloth-config.dependencies.short_description.single", getEntry().getFieldName(), condition);
    }
    
    public void useActualText(boolean shouldUseActualText) {
        this.useActualText = shouldUseActualText;
    }
}
