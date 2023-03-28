package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A record defining a dependency to be built.
 * Can be declared using either an {@link ConfigEntry.Gui.EnableIf @EnableIf} or {@link ConfigEntry.Gui.ShowIf @ShowIf} annotation.
 *
 * @param i18n the absolute i18n key of the depended-on config entry
 * @param conditions flagged strings to be parsed into dependency conditions
 * @param matching flagged strings to be parsed into dynamic dependency conditions
 * @see DependencyGroupDefinition
 */
@ApiStatus.Internal
record DependencyDefinition(String i18n, Set<StaticConditionDefinition> conditions, Set<MatcherConditionDefinition> matching) {
    
    DependencyDefinition(@Nullable String i18nBase, ConfigEntry.Gui.EnableIf annotation) {
        this(i18nBase, annotation.value(), annotation.conditions(), annotation.matching());
    }
    
    DependencyDefinition(@Nullable String i18nBase, ConfigEntry.Gui.ShowIf annotation) {
        this(i18nBase, annotation.value(), annotation.conditions(), annotation.matching());
    }
    
    private DependencyDefinition(@Nullable String i18nBase, String i18nKey, String[] conditions, String[] matching) {
        this(
                DependencyManager.parseRelativeI18n(i18nBase, i18nKey),
                Arrays.stream(conditions)
                        .map(StaticConditionDefinition::fromConditionString)
                        .collect(Collectors.toUnmodifiableSet()), 
                Arrays.stream(matching)
                        .map((String condition) -> MatcherConditionDefinition.fromConditionString(i18nBase, condition))
                        .collect(Collectors.toUnmodifiableSet()));
    }
}
