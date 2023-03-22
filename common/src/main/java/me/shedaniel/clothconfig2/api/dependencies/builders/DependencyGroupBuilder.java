package me.shedaniel.clothconfig2.api.dependencies.builders;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.DependencyGroup;

import java.util.*;

public class DependencyGroupBuilder implements DependencyBuilder<DependencyGroup, DependencyGroupBuilder> {
    
    private final Set<Dependency> children = new HashSet<>();
    
    private DependencyGroup.Condition condition = DependencyGroup.Condition.ALL;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Boolean> hidden = Optional.empty();
    
    public DependencyGroupBuilder() {
    }
    
    @Override
    public DependencyGroup build() {
        DependencyGroup group = new DependencyGroup(this.condition);
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
}
