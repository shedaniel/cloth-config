package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MatcherCondition;
import net.minecraft.network.chat.Component;

public class GenericMatcherCondition<T> extends FlaggedCondition<T> implements MatcherCondition<T>, EqualityCondition<T> {
    private final ConfigEntry<T> gui;
    
    public GenericMatcherCondition(ConfigEntry<T> gui) {
        this.gui = gui;
    }
    
    @Override
    public Component getText(boolean inverted) {
        return gui.getFieldName();
    }
    
    @Override
    public ConfigEntry<T> getElement() {
        return gui;
    }
}
