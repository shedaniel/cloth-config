package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.FinishDependencyBuilder;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractDependencyBuilder<C, D extends AbstractDependency<? super C>, SELF extends AbstractDependencyBuilder<C, D, SELF>> implements FinishDependencyBuilder<D, SELF> {
    protected static final int minConditions = 1;
    protected final Set<C> conditions = new HashSet<>();
    protected GroupRequirement requirement = GroupRequirement.ANY;
    protected boolean inverted = false;
    protected boolean displayTooltips = true;
    
    private Function<String, Component[]> tooltipProvider = null;
    private Component description = null;
    
    @Override
    public SELF displayTooltips(boolean showTooltips) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.displayTooltips = showTooltips;
        return self;
    }
    
    /**
     * Finishes building the given {@code dependency} by applying anything defined in this abstract class, for example
     * applying any conditions.
     * <br><br>
     * Should be used by implementations of {@link #build()}.
     * 
     * @param dependency the dependency to finish building
     * @return the built dependency
     */
    protected D finishBuilding(D dependency) {
        if (conditions.size() < minConditions)
            throw new IllegalArgumentException("%s requires at least %d condition%s.".formatted(dependency.getClass().getSimpleName(), minConditions, minConditions == 1 ? "" : "s"));
        
        if (tooltipProvider == null)
            tooltipProvider = generateTooltipProvider();
        if (description == null)
            description = generateDescription();
    
        dependency.setRequirement(this.requirement.inverted(inverted));
        dependency.addConditions(this.conditions);
        dependency.displayTooltips(this.displayTooltips);
        dependency.setDescription(this.description);
        dependency.setTooltipProvider(this.tooltipProvider);
        return dependency;
    }
    
    @Override
    public SELF inverted(boolean inverted) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.inverted = inverted;        
        return self;
    }
    
    @Override
    public SELF setTooltipProvider(Function<String, Component[]> tooltipProvider) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.tooltipProvider = tooltipProvider;
        return self;
    }
    
    @Override
    public SELF setDescription(Component description) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.description = description;
        return self;
    }
    
    protected abstract Component generateDescription();
    
    protected abstract Function<String, Component[]> generateTooltipProvider();
}
