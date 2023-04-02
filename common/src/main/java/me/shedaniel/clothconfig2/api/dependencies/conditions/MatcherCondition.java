package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.FlaggedCondition;
import net.minecraft.network.chat.Component;

public class MatcherCondition<T> extends FlaggedCondition<T> {
    private final ConfigEntry<T> gui;
    
    public MatcherCondition(ConfigEntry<T> gui) {
        this.gui = gui;
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
