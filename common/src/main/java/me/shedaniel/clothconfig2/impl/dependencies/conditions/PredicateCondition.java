package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.NewCondition;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class PredicateCondition<T> implements NewCondition<T> {
    
    private final Predicate<T> predicate;
    private final Component description;
    private final Supplier<Component> describer;
    
    
    PredicateCondition(Predicate<T> predicate, Component description) {
        this(predicate, description, null);
    }
    
    PredicateCondition(Predicate<T> predicate, Supplier<Component> describer) {
        this(predicate, null, describer);
    }
    
    private PredicateCondition(Predicate<T> predicate, @Nullable Component description, @Nullable Supplier<Component> describer) {
        if (description == null && describer == null)
            throw new IllegalArgumentException("description and describer cannot both be null");
        
        this.predicate = predicate;
        this.description = description;
        this.describer = describer;
        
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
}
