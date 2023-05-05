package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;

// FIXME rename to GroupDependency
public class DependencyGroup extends AbstractDependency<Dependency> {
    
    DependencyGroup() {}
    
    @Override
    public boolean check() {
        return this.getRequirement().matches(this.getConditions(), Dependency::check);
    }
}
