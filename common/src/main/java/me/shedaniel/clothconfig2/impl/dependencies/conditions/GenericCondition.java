package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConditionFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.EnumSet;
import java.util.function.BiPredicate;


public class GenericCondition<T> implements Condition<T> {
    
    private final EnumSet<ConditionFlag> flags = EnumSet.noneOf(ConditionFlag.class);
    private final String value;
    
    public GenericCondition(T value) {
        this.value = String.valueOf(value);
    }
    
    public GenericCondition(Class<T> type, String value) {
        this.value = value;
    }
    
    @Override
    public boolean check(T value) {
        BiPredicate<String, String> equality = ignoreCase() ? String::equalsIgnoreCase : String::equals;
        
        return inverted() != equality.test(this.value, String.valueOf(value));
    }
    
    public final boolean ignoreCase() {
        return getFlags().contains(ConditionFlag.IGNORE_CASE);
    }
    
    @Override
    public Component getText(boolean inverted) {
        MutableComponent text = Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                Component.translatable("text.cloth-config.quoted", Component.literal(this.value)));
        
        return inverted() ? Component.translatable("text.cloth-config.dependencies.conditions.not", text) : text;
    }
    
    @Override
    public EnumSet<ConditionFlag> getFlags() {
        return this.flags;
    }
}
