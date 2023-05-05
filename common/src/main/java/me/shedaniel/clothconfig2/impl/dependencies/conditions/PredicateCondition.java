package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class PredicateCondition<T> implements Condition<T> {
    
    private final Predicate<T> predicate;
    private final Component description;
    private final Supplier<Component> describer;
    private final String adjectiveKey;
    private final String negativeAdjectiveKey;
    
    
    PredicateCondition(Predicate<T> predicate, Component description, String adjectiveKey, String negativeAdjectiveKey) {
        this(predicate, description, null, adjectiveKey, negativeAdjectiveKey);
    }
    
    PredicateCondition(Predicate<T> predicate, Supplier<Component> describer, String adjectiveKey, String negativeAdjectiveKey) {
        this(predicate, null, describer, adjectiveKey, negativeAdjectiveKey);
    }
    
    private PredicateCondition(Predicate<T> predicate, @Nullable Component description, @Nullable Supplier<Component> describer, String adjectiveKey, String negativeAdjectiveKey) {
        if (description == null && describer == null)
            throw new IllegalArgumentException("description and describer cannot both be null");
        
        this.predicate = predicate;
        this.description = description;
        this.describer = describer;
        this.adjectiveKey = adjectiveKey;
        this.negativeAdjectiveKey = negativeAdjectiveKey;
    }
    
    @Override
    public boolean check(T value) {
        return predicate.test(value);
    }
    
    @Override
    public Component description() {
        if (description != null)
            return description;
        if (describer != null)
            return describer.get();
        throw new IllegalStateException("description and describer cannot both be null");
    }
    
    @Override
    public Component fullDescription(boolean inverted) {
        String adjectiveKey = inverted ? negativeAdjectiveKey : this.adjectiveKey;
        return Component.translatable(adjectiveKey, description());
    }
}
