package me.shedaniel.autoconfig.gui.registry;

import me.shedaniel.autoconfig.dependencies.DependencyManager;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;

public abstract class AbstractGuiRegistry implements GuiRegistryAccess {
    
    private DependencyManager dependencyManager = new DependencyManager();
    
    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }
    
    @Override
    public void setDependencyManager(DependencyManager manager) {
        dependencyManager = manager;
    }
    
}
