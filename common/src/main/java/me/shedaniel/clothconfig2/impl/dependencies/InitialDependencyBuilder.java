package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

public class InitialDependencyBuilder {
    
    private static final InitialDependencyBuilder INSTANCE = new InitialDependencyBuilder();
    
    private InitialDependencyBuilder() {}
    
    /**
     * @deprecated use {@link Dependency#builder()} instead.
     */
    @Contract(pure = true)
    @ApiStatus.Internal
    @Deprecated
    public static InitialDependencyBuilder getBuilder() {
        return INSTANCE;
    }
    
    public BooleanDependencyBuilder dependingOn(BooleanListEntry gui) {
        return new BooleanDependencyBuilder(gui);
    }
    
    public <T extends Enum<?>> EnumDependencyBuilder<T> dependingOn(EnumListEntry<T> gui) {
        return new EnumDependencyBuilder<>(gui);
    }
    
    public <T extends Number & Comparable<T>> NumberDependencyBuilder<T> dependingOn(NumberConfigEntry<T> gui) {
        return new NumberDependencyBuilder<>(gui);
    }
    
    public <T, E extends ConfigEntry<T>> GenericDependencyBuilder<T> dependingOn(E type) {
        return new GenericDependencyBuilder<>(type);
    }
    
    public DependencyGroupBuilder startGroup() {
        return new DependencyGroupBuilder();
    }
}
