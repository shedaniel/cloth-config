package me.shedaniel.clothconfig2.api.dependencies.requirements;

public interface Requirement<SELF> {
    SELF inverted(boolean inverted);
}
