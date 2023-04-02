package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.StartDependencyBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

public class InitialDependencyBuilder implements StartDependencyBuilder {
    
    private static final InitialDependencyBuilder INSTANCE = new InitialDependencyBuilder();
    
    private boolean tooltip = true;
    
    private InitialDependencyBuilder() {}
    
    /**
     * @deprecated use {@link Dependency#builder()} instead.
     */
    @Contract(pure = true)
    @ApiStatus.Internal
    @Deprecated
    public static StartDependencyBuilder getBuilder() {
        return INSTANCE;
    }
    
    @Override
    public DependencyGroupBuilder startGroup() {
        return new DependencyGroupBuilder();
    }
    
    @Override
    public BooleanDependencyBuilder dependingOn(BooleanListEntry gui) {
        return new BooleanDependencyBuilder(gui)
                .generateTooltip(tooltip);
    }
    
    @Override
    public <T extends Enum<?>> EnumDependencyBuilder<T> dependingOn(EnumListEntry<T> gui) {
        return new EnumDependencyBuilder<>(gui)
                .generateTooltip(tooltip);
    }
    
    @Override
    public <T extends Number & Comparable<T>> NumberDependencyBuilder<T> dependingOn(NumberConfigEntry<T> gui) {
        return new NumberDependencyBuilder<>(gui)
                .generateTooltip(tooltip);
    }
    
    @Override
    public <T, E extends ConfigEntry<T>> GenericDependencyBuilder<T> dependingOn(E type) {
        return new GenericDependencyBuilder<>(type)
                .generateTooltip(tooltip);
    }
    
    @Override
    public StartDependencyBuilder generateTooltip(boolean shouldGenerate) {
        this.tooltip = shouldGenerate;
        return this;
    }
}
