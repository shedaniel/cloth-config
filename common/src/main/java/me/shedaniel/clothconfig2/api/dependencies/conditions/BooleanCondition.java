package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

public class BooleanCondition extends Condition<Boolean> {
    public BooleanCondition(Boolean value) {
        super(value);
    }
    
    @Override
    public boolean check(Boolean value) {
        return inverted() != (this.value == value);
    }
    
    @Override
    public Component getText() {
        return Component.translatable("text.cloth-config.dependencies.conditions.%s"
                .formatted(inverted() != this.value ? "enabled" : "disabled"));
    }
}
