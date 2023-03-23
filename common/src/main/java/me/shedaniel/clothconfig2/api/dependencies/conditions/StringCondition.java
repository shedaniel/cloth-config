package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.function.BiPredicate;

public class StringCondition extends Condition<String> {
    
    public StringCondition(String value) {
        super(value);
    }
    
    @Override
    protected boolean matches(String value) {
        BiPredicate<String, String> equality = ignoreCase() ? String::equalsIgnoreCase : String::equals;
    
        return equality.test(getValue(), value);
    }
    
    public final boolean ignoreCase() {
        return getFlags().contains(Flag.IGNORE_CASE);
    }
    
    @Override
    protected Component getTextInternal() {
        return Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                Component.translatable("text.cloth-config.quoted", Component.literal(getValue())));
    }
    
    private String applyCaseFlags(String string) {
        return ignoreCase() ? string.toLowerCase() : string;
    }
}
