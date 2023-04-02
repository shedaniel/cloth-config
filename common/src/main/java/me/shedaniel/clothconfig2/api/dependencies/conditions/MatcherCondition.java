package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import net.minecraft.network.chat.Component;

import java.util.EnumSet;

public class MatcherCondition<T> implements Condition<T> {
    private final ConfigEntry<T> gui;
    private final EnumSet<ConditionFlag> flags = EnumSet.noneOf(ConditionFlag.class);
    
    public MatcherCondition(ConfigEntry<T> gui) {
        this.gui = gui;
    }
    
    @Override
    public EnumSet<ConditionFlag> getFlags() {
        return flags;
    }
    
    /**
     * Checks if the condition is met by the provided value.
     * 
     * @param value the value to check against this condition
     * @return whether {@code value} satisfies this condition
     */
    @Override
    public boolean check(T value) {
        return inverted() != matches(value);
    }
    
    protected boolean matches(T value) {
        return getElement().getValue().equals(value);
    }
    
    @Override
    public Component getText(boolean inverted) {
        return gui.getFieldName();
    }
    
    public ConfigEntry<T> getElement() {
        return gui;
    }
}
