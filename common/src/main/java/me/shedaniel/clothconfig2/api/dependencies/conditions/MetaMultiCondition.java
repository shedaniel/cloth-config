package me.shedaniel.clothconfig2.api.dependencies.conditions;

import java.util.Collection;

public interface MetaMultiCondition<T extends Comparable<T>> extends MultiCondition<T> {
    @Override
    default boolean check(Collection<T> values) {
        // FIXME does it make more sense to use GroupRequirement(ALL/ANY/NONE/ONE/ETC) than ContainmentRequirement
        //  since we're not really checking for containment, rather were checkin how many entries match the sub-condition.
        return getRequirement().check(getValue(), getSubCondition());
    }
    
    Condition<T> getSubCondition();
}
