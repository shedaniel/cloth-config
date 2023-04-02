package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.*;

public interface StartDependencyBuilder extends DependencyBuilder<StartDependencyBuilder> {
    
    DependencyGroupBuilder startGroup();
    
    BooleanDependencyBuilder dependingOn(BooleanListEntry gui);
    
    <T extends Enum<?>> EnumDependencyBuilder<T> dependingOn(EnumListEntry<T> gui);
    
    <T extends Number & Comparable<T>> NumberDependencyBuilder<T> dependingOn(NumberConfigEntry<T> gui);
    
    <T, E extends ConfigEntry<T>> GenericDependencyBuilder<T> dependingOn(E type);
}
