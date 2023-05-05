package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Dependency.EnableIf;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Dependency.ShowIf;
import me.shedaniel.autoconfig.util.RelativeI18n;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.BooleanDependencyBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A record defining a dependency to be built.
 * Can be declared using either an {@link EnableIf @EnableIf} or {@link ShowIf @ShowIf} annotation.
 *
 * @param i18n the absolute i18n key of the depended-on config entry
 * @param tooltip whether the dependency should auto-generate tooltips
 * @param allowGeneric whether the dependency should still be built if a type-specific dependency is not supported
 * @param requirement the number of conditions required for this dependency to be met
 * @param conditions flagged strings to be parsed into dependency conditions
 * @param matching flagged strings to be parsed into dynamic dependency conditions
 * @see DependencyGroupDefinition
 */
record DependencyDefinition(String i18n, boolean tooltip, boolean allowGeneric, GroupRequirement requirement, Set<StaticConditionDefinition> conditions, Set<MatcherConditionDefinition> matching) {
    
    DependencyDefinition(@Nullable String i18nBase, EnableIf annotation) {
        this(i18nBase, annotation.value(), annotation.tooltip(), annotation.allowGeneric(), annotation.requirement(), annotation.conditions(), annotation.matching());
    }
    
    DependencyDefinition(@Nullable String i18nBase, ShowIf annotation) {
        this(i18nBase, annotation.value(), annotation.tooltip(), annotation.allowGeneric(), annotation.requirement(), annotation.conditions(), annotation.matching());
    }
    
    private DependencyDefinition(@Nullable String i18nBase, String i18nKey, boolean tooltip, boolean allowGeneric, GroupRequirement requirement, String[] conditions, String[] matching) {
        this(RelativeI18n.parse(i18nBase, i18nKey), tooltip, allowGeneric, requirement,
                Arrays.stream(conditions)
                        .map(StaticConditionDefinition::fromConditionString)
                        .collect(Collectors.toUnmodifiableSet()), 
                Arrays.stream(matching)
                        .map(condition -> MatcherConditionDefinition.fromConditionString(i18nBase, condition))
                        .collect(Collectors.toUnmodifiableSet()));
    }
    
    private <T extends Condition<?>> Set<T> buildConditions(Function<StaticConditionDefinition, T> builder) {
        return this.conditions().stream().map(builder).collect(Collectors.toUnmodifiableSet());
    }
    
    private <T> Set<Condition<T>> buildMatchers(Class<T> type, DependencyManager manager) {
        return this.matching().stream()
                .map(definition -> definition.toMatcher(manager.getEntry(type, definition.i18n())))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    private <T extends Comparable<T>> Set<Condition<T>> buildComparativeMatchers(Class<T> type, DependencyManager manager) {
        return this.matching().stream()
                .map(definition -> definition.toComparativeMatcher(manager.getEntry(type, definition.i18n())))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    /**
     * Build a {@link Dependency} as defined in this definition.
     * <br><br>
     * <p>Currently, supports depending on the following:
     * <ul>
     *     <li>{@link BooleanListEntry} entries</li>
     *     <li>{@link EnumListEntry} entries</li>
     *     <li>Entries implementing {@link NumberConfigEntry}</li>
     * </ul>
     * <p>Unsupported types can be used, but conditions will be checked using {@link String#valueOf(Object)}.
     * No guarantees are made for how this will function in practice.
     *
     * @param manager a DependencyManager that has all config entries registered
     * @return The built {@link Dependency}
     * @throws IllegalArgumentException if the defined dependency is invalid
     */
    public Dependency build(DependencyManager manager) {
        me.shedaniel.clothconfig2.api.ConfigEntry<?> gui = manager.getEntry(this.i18n());
        
        if (gui instanceof BooleanListEntry booleanListEntry)
            return this.build(manager, booleanListEntry);
        else if (gui instanceof EnumListEntry<?> enumListEntry)
            return this.build(manager, enumListEntry);
        else if (gui instanceof NumberConfigEntry<?> numberConfigEntry)
            return this.build(manager, numberConfigEntry);
        else if (this.allowGeneric())
            return this.buildGeneric(manager, gui);
        else
            throw new IllegalArgumentException("Unsupported dependency type: %s".formatted(gui.getClass().getSimpleName()));
    }
    
    /**
     * Builds the {@link Dependency defined in this definition, depending on the given {@link BooleanListEntry}.
     *
     * @param manager a DependencyManager that has all config entries registered
     * @param gui     the {@link BooleanListEntry} to be depended on
     * @return the generated dependency
     */
    public Dependency build(DependencyManager manager, BooleanListEntry gui) {
        Set<Condition<Boolean>> conditions = this.buildConditions(StaticConditionDefinition::toBooleanCondition);
        Set<Condition<Boolean>> matchers = this.buildMatchers(Boolean.class, manager);
        
        // Start building the dependency
        BooleanDependencyBuilder builder = Dependency.builder().dependingOn(gui);
        
        // BooleanDependencyBuilder supports zero or one requirement being set 
        if (!conditions.isEmpty()) {
            if (conditions.size() != 1)
                throw new IllegalArgumentException("Boolean dependencies require exactly one requirement, found " + conditions.size());
            
            conditions.forEach(builder::matching);
        }
        
        builder.matching(matchers);
        builder.displayTooltips(this.tooltip());
        return builder.build();
    }
    
    /**
     * Builds the {@link Dependency} defined in this definition, depending on the given {@link EnumListEntry}.
     *
     * @param manager a DependencyManager that has all config entries registered
     * @param gui     the {@link EnumListEntry} to be depended on
     * @return the generated dependency
     */
    public <T extends Enum<?>> Dependency build(DependencyManager manager, EnumListEntry<T> gui) {
        Class<T> type = gui.getType();
        Set<Condition<T>> conditions = this.buildConditions(condition -> condition.toEnumCondition(type));
        Set<Condition<T>> matchers = this.buildMatchers(type, manager);
    
        return Dependency.builder()
                .dependingOn(gui)
                .matching(conditions)
                .matching(matchers)
                .displayTooltips(this.tooltip())
                .build();
    }
    
    /**
     * Builds the {@link Dependency} defined in this definition, depending on the given {@link NumberConfigEntry}.
     *
     * @param manager a DependencyManager that has all config entries registered
     * @param gui     the {@link NumberConfigEntry} to be depended on
     * @return the generated dependency
     */
    public <T extends Number & Comparable<T>> Dependency build(DependencyManager manager, NumberConfigEntry<T> gui) {
        Class<T> type = gui.getType();
        Set<Condition<T>> conditions = this.buildConditions(condition -> condition.toNumberCondition(type));
        Set<Condition<T>> matchers = this.buildComparativeMatchers(type, manager);
        
        return Dependency.builder()
                .dependingOn(gui)
                .matching(conditions)
                .matching(matchers)
                .displayTooltips(this.tooltip())
                .build();
    }
    
    /**
     * Builds the {@link Dependency} defined in this definition, depending on the given {@link ConfigEntry}.
     * <p>
     * If available, you should consider using a type-specific builder such as {@link #build(DependencyManager, BooleanListEntry)}.
     * Generic dependencies are inherently limited because static conditions can only be checked using {@link Object#equals(Object)}.
     *
     * @param manager a DependencyManager that has all config entries registered
     * @param gui     the {@link ConfigEntry} to be depended on
     * @return the generated dependency
     */
    public <T> Dependency buildGeneric(DependencyManager manager, ConfigEntry<T> gui) {
        Class<T> type = gui.getType();
        Set<Condition<T>> conditions = this.buildConditions(condition -> condition.toGenericCondition(type));
        Set<Condition<T>> matchers = this.buildMatchers(type, manager);
        
        return Dependency.builder()
                .dependingOnGeneric(gui)
                .matching(conditions)
                .matching(matchers)
                .build();
    }
}
