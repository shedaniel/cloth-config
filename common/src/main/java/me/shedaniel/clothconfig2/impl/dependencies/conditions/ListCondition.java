package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListCondition<T> extends FlaggedCondition<List<T>> {
    
    private final ListRequirement requirement;
    private final Collection<T> values;
    
    public ListCondition(ListRequirement requirement, T value){
        this(requirement, Collections.singletonList(value));
    }
    
    public ListCondition(ListRequirement requirement, Collection<T> values){
        this.requirement = requirement;
        this.values = values;
    }
    
    @Override
    public boolean check(List<T> values) {
        return inverted() != this.requirement.check(values, this.values);
    }
    
    @Override
    public Component getText(boolean inverted) {
        //TODO
        return null;
    }
    
    public enum ListRequirement {
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
    }
}
