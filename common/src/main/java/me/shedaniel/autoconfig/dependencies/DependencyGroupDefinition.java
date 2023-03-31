package me.shedaniel.autoconfig.dependencies;


import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.GroupRequirement;
import me.shedaniel.clothconfig2.impl.dependencies.DependencyGroup;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A record defining a group dependency to be built.
 * Can be declared using either an {@link ConfigEntry.Gui.EnableIfGroup @EnableIfGroup} or {@link ConfigEntry.Gui.ShowIfGroup @ShowIfGroup} annotation.
 *
 * @param requirement the {@link GroupRequirement requirement} defining how many children must be met
 * @param inverted whether the dependency group should be logically inverted
 * @param tooltip whether the dependency group should auto-generate tooltips
 * @param children dependency definitions to be included in the group
 * @see DependencyDefinition
 */
@ApiStatus.Internal
record DependencyGroupDefinition(GroupRequirement requirement, boolean inverted, boolean tooltip, Set<DependencyDefinition> children) {
    DependencyGroupDefinition(@Nullable String prefix, String i18nBase, ConfigEntry.Gui.EnableIfGroup annotation) {
        this(annotation.condition(), annotation.inverted(), annotation.tooltip(),
                Arrays.stream(annotation.value())
                        .map(child -> new DependencyDefinition(prefix, i18nBase, child))
                        .collect(Collectors.toUnmodifiableSet()));
    }
    DependencyGroupDefinition(@Nullable String prefix, String i18nBase, ConfigEntry.Gui.ShowIfGroup annotation) {
        this(annotation.condition(), annotation.inverted(), annotation.tooltip(),
                Arrays.stream(annotation.value())
                        .map(child -> new DependencyDefinition(prefix, i18nBase, child))
                        .collect(Collectors.toUnmodifiableSet()));
    }
    
    Set<Dependency> buildChildren(Function<DependencyDefinition, Dependency> builder) {
        return this.children().stream().map(builder).collect(Collectors.toUnmodifiableSet());
    }
    
    /**
     * Build a {@link DependencyGroup} as defined in the {@link DependencyGroupDefinition definition}.
     * <br><br>
     * If there is an issue building any child dependency, a {@link RuntimeException} will be thrown.
     *
     * @param manager a DependencyManager that has all config entries registered
     * @return The built {@link DependencyGroup}
     * @throws RuntimeException when there is an issue building one of the group's dependencies
     */
    public Dependency build(DependencyManager manager) {
        // Build each child DependencyDefinition
        Set<Dependency> dependencies = this.buildChildren(child -> child.build(manager));
        
        // If there's only one child, don't bother making a group
        // unless the group is being used to invert the child
        if (dependencies.size() == 1) {
            boolean invert = switch (this.requirement()) {
                case ALL, ANY, ONE -> this.inverted();
                case NONE, NOT_ALL, NOT_ONE -> !this.inverted();
            };
            if (!invert)
                return dependencies.iterator().next();
        }
        
        // Build and return the DependencyGroup
        return Dependency.groupBuilder()
                .generateTooltip(this.tooltip())
                .inverted(this.inverted())
                .withCondition(this.requirement())
                .withChildren(dependencies)
                .build();
    }
}
