package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConfigEntryMatcher;
import me.shedaniel.clothconfig2.api.dependencies.conditions.StaticCondition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A record defining a dependency to be built.
 * Can be declared using either an {@link ConfigEntry.Gui.EnableIf @EnableIf} or {@link ConfigEntry.Gui.ShowIf @ShowIf} annotation.
 *
 * @param i18n the absolute i18n key of the depended-on config entry
 * @param tooltip whether the dependency should auto-generate tooltips
 * @param conditions flagged strings to be parsed into dependency conditions
 * @param matching flagged strings to be parsed into dynamic dependency conditions
 * @see DependencyGroupDefinition
 */
@ApiStatus.Internal
record DependencyDefinition(String i18n, boolean tooltip, Set<StaticConditionDefinition> conditions, Set<MatcherConditionDefinition> matching) {
    
    DependencyDefinition(@Nullable String i18nBase, ConfigEntry.Gui.EnableIf annotation) {
        this(i18nBase, annotation.value(), annotation.tooltip(), annotation.conditions(), annotation.matching());
    }
    
    DependencyDefinition(@Nullable String i18nBase, ConfigEntry.Gui.ShowIf annotation) {
        this(i18nBase, annotation.value(), annotation.tooltip(), annotation.conditions(), annotation.matching());
    }
    
    private DependencyDefinition(@Nullable String i18nBase, String i18nKey, boolean tooltip, String[] conditions, String[] matching) {
        this(DependencyManager.parseRelativeI18n(i18nBase, i18nKey), tooltip,
                Arrays.stream(conditions)
                        .map(StaticConditionDefinition::fromConditionString)
                        .collect(Collectors.toUnmodifiableSet()), 
                Arrays.stream(matching)
                        .map(condition -> MatcherConditionDefinition.fromConditionString(i18nBase, condition))
                        .collect(Collectors.toUnmodifiableSet()));
    }
    
    <T extends StaticCondition<?>> Set<T> buildConditions(Function<StaticConditionDefinition, T> mapper) {
        return this.conditions().stream().map(mapper).collect(Collectors.toUnmodifiableSet());
    }
    
    <T> Set<ConfigEntryMatcher<T>> buildMatchers(Class<T> type, BiFunction<Class<T>, String, ? extends me.shedaniel.clothconfig2.api.ConfigEntry<T>> getEntry) {
        return this.matching().stream()
                .map(def -> def.toMatcher(getEntry.apply(type, def.i18n())))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    <T extends Comparable<T>> Set<ConfigEntryMatcher<T>> buildComparableMatchers(Class<T> type, BiFunction<Class<T>, String, ? extends me.shedaniel.clothconfig2.api.ConfigEntry<T>> getEntry) {
        return this.matching().stream()
                .map(def -> def.toComparableMatcher(getEntry.apply(type, def.i18n())))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    
}
