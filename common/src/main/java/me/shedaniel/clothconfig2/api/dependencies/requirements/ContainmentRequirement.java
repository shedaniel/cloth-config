package me.shedaniel.clothconfig2.api.dependencies.requirements;

import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import net.minecraft.network.chat.Component;

import java.util.Collection;

public enum ContainmentRequirement implements Requirement<ContainmentRequirement> {
    
    CONTAINS_ANY,
    NOT_CONTAINS_ANY,
    CONTAINS_ALL,
    NOT_CONTAINS_ALL,
    MATCHES,
    NOT_MATCHES;
    
    /**
     * Checks whether {@code collection} meets this requirement for {@code values}
     *
     * <ul>
     *     <li><em>{@link #CONTAINS_ANY}</em> - true if {@code collection} contains anything in {@code values}</li>
     *     <li><em>{@link #NOT_CONTAINS_ANY}</em> - true if {@code collection} doesn't contain anything in {@code values}</li>
     *     <li><em>{@link #CONTAINS_ALL}</em> - true if {@code collection} contains everything in {@code values}</li>
     *     <li><em>{@link #NOT_CONTAINS_ALL}</em> - true if {@code collection} doesn't contain everything in {@code values}</li>
     *     <li><em>{@link #MATCHES}</em> - true if {@code collection}'s content exactly matches {@code values}'s</li>
     *     <li><em>{@link #NOT_MATCHES}</em> - true if {@code collection}'s content is different to {@code values}'s</li>
     * </ul>
     */
    public <T> boolean check(Collection<T> collection, Collection<T> values) {
        return switch (this) {
            case CONTAINS_ANY -> values.stream().anyMatch(collection::contains);
            case NOT_CONTAINS_ANY -> values.stream().noneMatch(collection::contains);
            case CONTAINS_ALL -> collection.containsAll(values);
            case NOT_CONTAINS_ALL -> values.stream().anyMatch(value -> !collection.contains(value));
            case MATCHES -> values.size() == collection.size() && values.containsAll(collection);
            case NOT_MATCHES -> values.size() != collection.size()
                                || values.stream().anyMatch(value -> !collection.contains(value));
        };
    }
    
    /**
     * Checks whether {@code collection} meets this requirement when tested using {@code condition}
     */
    public <T> boolean check(Collection<T> collection, Condition<T> condition) {
        return switch (this) {
            case CONTAINS_ANY -> collection.stream().anyMatch(condition::check);
            case NOT_CONTAINS_ANY -> collection.stream().noneMatch(condition::check);
            case CONTAINS_ALL, MATCHES -> collection.stream().allMatch(condition::check);
            case NOT_CONTAINS_ALL, NOT_MATCHES -> collection.stream().anyMatch(value -> !condition.check(value));
        };
    }
    
    @Override
    public ContainmentRequirement inverted(boolean inverted) {
        if (!inverted)
            return this;
        
        return switch (this) {
            case CONTAINS_ANY -> NOT_CONTAINS_ANY;
            case NOT_CONTAINS_ANY -> CONTAINS_ANY;
            case CONTAINS_ALL -> NOT_CONTAINS_ALL;
            case NOT_CONTAINS_ALL -> CONTAINS_ALL;
            case MATCHES -> NOT_MATCHES;
            case NOT_MATCHES -> MATCHES;
        };
    }
    
    public Simplified simplified() {
        return switch (this) {
            case CONTAINS_ANY -> new Simplified(CONTAINS_ANY, true);
            case NOT_CONTAINS_ANY -> new Simplified(CONTAINS_ANY, false);
            case CONTAINS_ALL -> new Simplified(CONTAINS_ALL, true);
            case NOT_CONTAINS_ALL -> new Simplified(CONTAINS_ALL, false);
            case MATCHES -> new Simplified(MATCHES, true);
            case NOT_MATCHES -> new Simplified(MATCHES, false);
        };
    }
    
    public Component getText() {
        return Component.translatable("text.cloth-config.containment_requirement.%s".formatted(this.name().toLowerCase()));
    }
    
    public record Simplified(ContainmentRequirement requirement, boolean condition) {
        
        // TODO move to static/matcher builder
        public Component describe() {
            // [has|doesn't have] [any|all|exactly] of the following: __
            Component adjective = Component.translatable("text.cloth-config.containment_requirement.%s".formatted(this.condition() ? "has" : "not_has"));
            Component quantity = this.requirement().getText();
            
            return Component.translatable("text.cloth-config.containment_requirement.description", adjective, quantity);
        }
    }
}
