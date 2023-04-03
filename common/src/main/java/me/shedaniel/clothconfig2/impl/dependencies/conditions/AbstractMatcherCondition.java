package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.MatcherCondition;
import net.minecraft.network.chat.Component;

public abstract class AbstractMatcherCondition<T> extends FlaggedCondition<T> implements MatcherCondition<T> {
    
    private final ConfigEntry<T> gui;
    
    protected AbstractMatcherCondition(ConfigEntry<T> gui) {
        this.gui = gui;
    }
    
    @Override
    public ConfigEntry<T> getElement() {
        return this.gui;
    }
    
    @Override
    public Component getText(boolean inverted) {
        // TODO
        return null;
    }
}
