package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public class MatcherConditionBuilder<T> extends SimpleConditionBuilder<T, MatcherConditionBuilder<T>> {

    private final ConfigEntry<T> otherGui;
    
    public MatcherConditionBuilder(ConfigEntry<T> gui) {
        this.otherGui = gui;
    }
    
    @Override
    protected Predicate<T> buildPredicate() {
        return value -> this.otherGui.getValue().equals(value);
    }
    
    @Override
    protected Component buildDescription() {
        return Component.translatable("text.cloth-config.dependencies.matches", this.otherGui.getFieldName());
    }
}
