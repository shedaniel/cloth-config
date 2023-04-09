package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;
import net.minecraft.network.chat.Component;

public class BooleanStaticCondition extends AbstractStaticCondition<Boolean> implements EqualityCondition<Boolean> {
    public BooleanStaticCondition(Boolean value) {
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
}
