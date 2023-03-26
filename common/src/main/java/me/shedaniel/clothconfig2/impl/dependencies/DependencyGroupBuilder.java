package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.DependencyBuilder;

import java.util.*;

public class DependencyGroupBuilder implements DependencyBuilder<DependencyGroup, DependencyGroupBuilder> {
    
    private final Set<Dependency> children = new HashSet<>();
    
    private DependencyGroup.Condition condition = DependencyGroup.Condition.ALL;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Boolean> hidden = Optional.empty();
    private boolean inverted = false;
    
    public DependencyGroupBuilder() {}
    
    @Override
    public DependencyGroup build() {
        DependencyGroup group = new DependencyGroup(this.condition, this.inverted);
        group.addChildren(this.children);
        hidden.ifPresent(group::hiddenWhenNotMet);
        return group;
    }
    
    public DependencyGroupBuilder withCondition(DependencyGroup.Condition condition) {
        this.condition = condition;
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
    
    @Override
    public DependencyGroupBuilder hideWhenNotMet(boolean shouldHide) {
        this.hidden = Optional.of(shouldHide);
        return this;
    }
    
    public DependencyGroupBuilder defaultHideBehaviour() {
        this.hidden = Optional.empty();
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
