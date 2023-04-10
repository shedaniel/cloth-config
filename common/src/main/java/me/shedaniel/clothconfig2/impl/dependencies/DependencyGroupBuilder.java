package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.FinishDependencyBuilder;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DependencyGroupBuilder implements FinishDependencyBuilder<DependencyGroup, DependencyGroupBuilder> {
    
    private final Set<Dependency> children = new HashSet<>();
    
    private GroupRequirement requirement = GroupRequirement.ALL;
    private boolean inverted = false;
    private boolean tooltip = true;
    
    public DependencyGroupBuilder() {}
    
    @Override
    public DependencyGroup build() {
        DependencyGroup group = new DependencyGroup();
        group.shouldGenerateTooltip(this.tooltip);
        group.setRequirement(this.requirement.inverted(this.inverted));
        group.addConditions(this.children);
        return group;
    }
    
    @Override
    public DependencyGroupBuilder generateTooltip(boolean shouldGenerate) {
        this.tooltip = shouldGenerate;
        return this;
    }
    
    @Override
    public DependencyGroupBuilder withRequirement(GroupRequirement requirement) {
        this.requirement = requirement;
        return this;
    }
    
    public DependencyGroupBuilder withChildren(Dependency... dependencies) {
        Collections.addAll(this.children, dependencies);
        return this;
    }
    
    public DependencyGroupBuilder withChildren(Collection<Dependency> dependencies) {
        this.children.addAll(dependencies);
        return this;
    }
    
    /**
     * The dependency condition will be inverted.
     * 
     * @return this instance, for chaining
     */
    public DependencyGroupBuilder inverted() {
        return inverted(true);
    }
    
    /**
     * Set whether the dependency condition should be inverted.
     *
     * @param inverted whether the condition should be inverted
     * @return this instance, for chaining
     */
    public DependencyGroupBuilder inverted(boolean inverted) {
        this.inverted = inverted;
        return this;
    }
}
