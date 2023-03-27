package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

public class BooleanCondition extends Condition<Boolean> {
    public BooleanCondition(Boolean value) {
        super(value);
    }
    
    public static BooleanCondition fromString(String condition) throws IllegalArgumentException {
        // The switch expression is functionally equivalent to Boolean::parseBoolean,
        // but allows us to throw a RuntimeException
        String string = condition.strip().toLowerCase();
        return new BooleanCondition(switch (string) {
            case "true" -> true;
            case "false" -> false;
            default ->
                    throw new IllegalStateException("Unexpected condition \"%s\" for Boolean dependency (expected \"true\" or \"false\").".formatted(string));
        });
    }
    
    public static BooleanCondition fromConditionString(String condition) throws IllegalArgumentException {
        Condition.Flag.FlaggedString record = Condition.Flag.fromConditionString(condition);
        BooleanCondition booleanCondition = fromString(record.condition());
        booleanCondition.setFlags(record.flags());
        return booleanCondition;
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
