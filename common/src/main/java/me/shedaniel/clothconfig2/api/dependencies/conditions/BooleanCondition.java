package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

public class BooleanCondition extends Condition<Boolean> {
    public BooleanCondition(Boolean value) {
        super(value);
    }
    
    @Override
    public Component getText(boolean inverted) {
        // For booleans, we can handle inversion ourselves
        // No need to call super.getText()
        boolean invert = inverted != inverted();
        return Component.translatable("text.cloth-config.dependencies.conditions.%s"
                .formatted(invert != getValue() ? "enabled" : "disabled"));
    }
    
    @Override
    protected Component getTextInternal() {
        return getText(false);
    }
}
