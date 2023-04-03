package me.shedaniel.clothconfig2.api.dependencies.conditions;

public interface ConditionRequirement<SELF> {
    SELF inverted(boolean inverted);
}
