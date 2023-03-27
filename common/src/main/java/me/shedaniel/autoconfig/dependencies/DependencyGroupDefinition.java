package me.shedaniel.autoconfig.dependencies;


import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.impl.dependencies.DependencyGroup;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

/**
 * A record defining a group dependency to be built.
 * Can be declared using either an {@link ConfigEntry.Gui.EnableIfGroup @EnableIfGroup} or {@link ConfigEntry.Gui.ShowIfGroup @ShowIfGroup} annotation.
 *
 * @param condition the {@link DependencyGroup.Condition condition} defining how many children must be met
 * @param inverted whether the dependency group should be logically inverted
 * @param children an array of dependency {@link DependencyDefinition definitions} to be included in the group
 * @see DependencyDefinition
 */
@ApiStatus.Internal
record DependencyGroupDefinition(DependencyGroup.Condition condition, boolean inverted, DependencyDefinition[] children) {
    DependencyGroupDefinition(String i18nBase, ConfigEntry.Gui.EnableIfGroup annotation) {
        this(annotation.condition(), annotation.inverted(),
                Arrays.stream(annotation.value())
                        .map(child -> new DependencyDefinition(i18nBase, child))
                        .toArray(DependencyDefinition[]::new));
    }
    DependencyGroupDefinition(String i18nBase, ConfigEntry.Gui.ShowIfGroup annotation) {
        this(annotation.condition(), annotation.inverted(),
                Arrays.stream(annotation.value())
                        .map(child -> new DependencyDefinition(i18nBase, child))
                        .toArray(DependencyDefinition[]::new));
    }
}
