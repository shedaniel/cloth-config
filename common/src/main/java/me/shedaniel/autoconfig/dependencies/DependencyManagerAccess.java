package me.shedaniel.autoconfig.dependencies;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Deprecated
public interface DependencyManagerAccess {
    @ApiStatus.Internal
    @Deprecated
    DependencyManager getDependencyManager();
    @ApiStatus.Internal
    @Deprecated
    void setDependencyManager(DependencyManager manager);
}
