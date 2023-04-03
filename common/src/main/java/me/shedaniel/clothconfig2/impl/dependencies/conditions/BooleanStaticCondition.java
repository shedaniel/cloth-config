package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;
import net.minecraft.network.chat.Component;

public class BooleanStaticCondition extends AbstractStaticCondition<Boolean> implements EqualityCondition<Boolean> {
    public BooleanStaticCondition(Boolean value) {
        super(value);
    }
    
    public static BooleanStaticCondition fromString(String condition) throws IllegalArgumentException {
        // The switch expression is functionally equivalent to Boolean::parseBoolean,
        // but allows us to throw a RuntimeException
        String string = condition.strip().toLowerCase();
        return new BooleanStaticCondition(switch (string) {
            case "true" -> true;
            case "false" -> false;
            default ->
                    throw new IllegalStateException("Unexpected condition \"%s\" for Boolean dependency (expected \"true\" or \"false\").".formatted(string));
        });
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
