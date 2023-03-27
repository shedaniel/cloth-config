package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

/**
 * Keeps track of a registered config entry and any associated dependency definitions
 *
 * @param gui the config entry 
 * @param enableIfDependencies a set of definitions that should enable/disable the config entry
 * @param enableIfGroups a set of group definitions that should enable/disable the config entry
 * @param showIfDependencies a set of definitions that should show/hide the config entry
 * @param showIfGroups a set of group definitions that should show/hide the config entry
 */
@ApiStatus.Internal
record EntryRecord(
        ConfigEntry<?> gui,
        Set<DependencyDefinition> enableIfDependencies,
        Set<DependencyGroupDefinition> enableIfGroups,
        Set<DependencyDefinition> showIfDependencies,
        Set<DependencyGroupDefinition> showIfGroups
) {
    /**
     * @return whether this config entry has any associated dependency definitions
     */
    boolean hasDependencies() {
        return !(
                enableIfDependencies().isEmpty() &&
                enableIfGroups().isEmpty() &&
                showIfDependencies().isEmpty() &&
                showIfGroups().isEmpty()
        );
    }
}
