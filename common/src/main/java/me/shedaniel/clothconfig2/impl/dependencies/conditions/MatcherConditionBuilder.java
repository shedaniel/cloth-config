package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public class MatcherConditionBuilder<T> extends SimpleConditionBuilder<T, MatcherConditionBuilder<T>> {

    private final ConfigEntry<T> otherGui;
    
    public MatcherConditionBuilder(ConfigEntry<T> gui) {
        this.otherGui = gui;
        this.adjectiveKey = "text.cloth-config.dependencies.matches";
        this.negativeAdjectiveKey = "text.cloth-config.dependencies.not_matches";
    }
    
    @Override
    protected Predicate<T> buildPredicate() {
        return value -> this.otherGui.getValue().equals(value);
    }
    
    @Override
    protected Component buildDescription() {
        return Component.translatable("text.cloth-config.quoted", this.otherGui.getFieldName());
    }
}
