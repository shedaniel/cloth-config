package me.shedaniel.clothconfig2.api.dependencies.conditions;

import java.util.Collection;

public enum ContainmentRequirement implements ConditionRequirement<ContainmentRequirement> {
    
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
        return switch (this) {
            case CONTAINS_ANY -> NOT_CONTAINS_ANY;
            case NOT_CONTAINS_ANY -> CONTAINS_ANY;
            case CONTAINS_ALL -> NOT_CONTAINS_ALL;
            case NOT_CONTAINS_ALL -> CONTAINS_ALL;
            case MATCHES -> NOT_MATCHES;
            case NOT_MATCHES -> MATCHES;
        };
    }
}
