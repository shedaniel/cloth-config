package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

public class BooleanCondition extends Condition<Boolean> {
    public BooleanCondition(Boolean value) {
        super(value);
    }
    
    @Override
    public Component getText() {
        // For booleans, we can handle inversion ourselves
        // No need to call super.getText()
        return getTextInternal();
    }
    
    @Override
    protected Component getTextInternal() {
        return Component.translatable("text.cloth-config.dependencies.conditions.%s"
                .formatted(inverted() != this.value ? "enabled" : "disabled"));
    }
}
