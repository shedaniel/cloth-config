package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnableIf;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnableIfGroup;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.ShowIf;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.ShowIfGroup;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConfigEntryMatcher;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.NumberCondition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@code DependencyManager} is used by {@link me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess GuiRegistryAccess} implementations
 * to handle generating dependencies defined in supported annotations.
 * <br><br>
 * It is necessary to register <strong>all</strong> config entry GUIs with the {@code DependencyManager} instance before building any dependencies.
 *
 * @see EnableIf @EnableIf
 * @see EnableIfGroup @EnableIfGroup
 * @see ShowIf @ShowIf
 * @see ShowIfGroup @ShowIfGroup
 * @see Dependency
 * @see me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess GuiRegistryAccess
 * @see DependencyManagerAccess
 */
public class DependencyManager {
    
    private static final char STEP_UP_PREFIX = '.';
    private static final char I18N_JOINER = '.';
    
    private final Map<String, EntryRecord> registry = new LinkedHashMap<>();
    
    private @Nullable String prefix;
    
    public DependencyManager() {}
    
    /**
     * Define a prefix which {@link #getEntry(String i18n)} will add
     * to i18n keys if they don't already start with it.
     * <br><br>
     * Should normally be set to the i18n key of the root {@link me.shedaniel.autoconfig.annotation.Config @Config}
     * class.
     * 
     * @param prefix the i18n prefix 
     */
    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
    }
    
    /**
     * Get the config entry GUI associated with the given i18n key.
     * If a prefix has been defined on this instance, it can optionally be ommitted
     * from the i18n key.
     *
     * @param i18n the i18n key 
     * @return An {@link Optional} containing config entry or {@code Optional.empty()}
     */
    public Optional<ConfigEntry<?>> getEntry(String i18n) {
        String key = prefix != null && i18n.startsWith(prefix) ?
                i18n : prefix + I18N_JOINER + i18n;
        return Optional.ofNullable(registry.get(key)).map(EntryRecord::gui);
    }
    
    /**
     * Gets the config entry GUI associated with the given i18n key, so long as the config entry can handle the
     * provided type.
     *
     * @param <T>  the type the GUI must support
     * @param type the type the GUI must support
     * @param i18n the i18n key
     * @return An {@link Optional} containing config entry or {@code Optional.empty()}
     */
    private <T> Optional<ConfigEntry<T>> getEntry(Class<T> type, String i18n) {
        return getEntry(i18n).map(entry -> {
                    // Entry's type must extend from the provided type 
                    if (!type.isAssignableFrom(entry.getType()))
                        return null;
                    
                    // If type is assignable, we can safely cast to <T>
                    @SuppressWarnings("unchecked") ConfigEntry<T> tEntry = (ConfigEntry<T>) entry;
                    return tEntry;
                });
    }
    
    /**
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     *
     * @param entry     the {@link me.shedaniel.autoconfig.annotation.ConfigEntry config entry} GUI to be registered
     * @param field     a {@link Field} which may have dependency annotations present
     * @param fieldI18n the i18n key of the field (not necessarily the GUI)
     * @see #buildDependencies()
     */
    public void register(ConfigEntry<?> entry, @Nullable Field field, @Nullable String fieldI18n) {
        Set<EnableIf> enableIfs = null;
        Set<EnableIfGroup> enableIfGroups = null;
        Set<ShowIf> showIfs = null;
        Set<ShowIfGroup> showIfGroups = null;
        
        if (field != null) {
            if (field.isAnnotationPresent(EnableIf.class))
                enableIfs = Collections.singleton(field.getAnnotation(EnableIf.class));
            if (field.isAnnotationPresent(EnableIfGroup.class))
                enableIfGroups = Collections.singleton(field.getAnnotation(EnableIfGroup.class));
            if (field.isAnnotationPresent(ShowIf.class))
                showIfs = Collections.singleton(field.getAnnotation(ShowIf.class));
            if (field.isAnnotationPresent(ShowIfGroup.class))
                showIfGroups = Collections.singleton(field.getAnnotation(ShowIfGroup.class));
        }
        
        String i18nBase = Optional.ofNullable(fieldI18n)
                .map(DependencyManager::i18nParent)
                .orElse(null);
    
        register(entry, i18nBase, enableIfs, enableIfGroups, showIfs, showIfGroups);
    }
    
    /**
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     * <br><br>
     * Explicitly provide one or more {@link EnableIf @EnableIf} or {@link EnableIfGroup @EnableIfGroup}
     * annotations to be applied to this entry.
     *
     * @param entry                    the config entry GUI
     * @param i18nBase                 an absolute i18n key, to be used as the base reference of the dependencies
     * @param enableIfAnnotations      a {@link Collection} of {@link EnableIf @EnableIf} annotations
     * @param enableIfGroupAnnotations a {@link Collection} of {@link EnableIfGroup @EnableIfGroup} annotations
     * @param showIfAnnotations        a {@link Collection} of {@link ShowIf @ShowIf} annotations
     * @param showIfGroupAnnotations   a {@link Collection} of {@link ShowIfGroup @ShowIfGroup} annotations
     * @see #register(ConfigEntry, Field, String)
     * @see #buildDependencies()
     */
    public void register(
            ConfigEntry<?> entry,
            @Nullable String i18nBase,
            @Nullable Collection<EnableIf> enableIfAnnotations,
            @Nullable Collection<EnableIfGroup> enableIfGroupAnnotations,
            @Nullable Collection<ShowIf> showIfAnnotations,
            @Nullable Collection<ShowIfGroup> showIfGroupAnnotations)
    {
        String i18n = entry.getI18nKey();
    
        // Get the already defined sets or create new ones
        Optional<EntryRecord> optional = Optional.ofNullable(registry.get(i18n));
        
        Set<DependencyDefinition> enableIfs = optional
                .map(EntryRecord::enableIfDependencies)
                .orElseGet(LinkedHashSet::new);
        
        Set<DependencyGroupDefinition> enableIfGroups = optional
                .map(EntryRecord::enableIfGroups)
                .orElseGet(LinkedHashSet::new);
        
        Set<DependencyDefinition> showIfs = optional
                .map(EntryRecord::showIfDependencies)
                .orElseGet(LinkedHashSet::new);
        
        Set<DependencyGroupDefinition> showIfGroups = optional
                .map(EntryRecord::showIfGroups)
                .orElseGet(LinkedHashSet::new);
    
        // Add the new dependencies to the appropriate sets
        enableIfs.addAll(Stream.ofNullable(enableIfAnnotations)
                .flatMap(Collection::stream)
                .map(single -> new DependencyDefinition(i18nBase, single))
                .collect(Collectors.toUnmodifiableSet()));
    
        enableIfGroups.addAll(Stream.ofNullable(enableIfGroupAnnotations)
                .flatMap(Collection::stream)
                .map(group -> new DependencyGroupDefinition(i18nBase, group))
                .collect(Collectors.toUnmodifiableSet()));
        
        showIfs.addAll(Stream.ofNullable(showIfAnnotations)
                .flatMap(Collection::stream)
                .map(single -> new DependencyDefinition(i18nBase, single))
                .collect(Collectors.toUnmodifiableSet()));
    
        showIfGroups.addAll(Stream.ofNullable(showIfGroupAnnotations)
                .flatMap(Collection::stream)
                .map(group -> new DependencyGroupDefinition(i18nBase, group))
                .collect(Collectors.toUnmodifiableSet()));
        
        registry.put(i18n, new EntryRecord(entry, enableIfs, enableIfGroups, showIfs, showIfGroups));
    }
    
    /**
     * Builds the dependencies for all registered config entries.
     * <br><br>
     * <strong>All config entries</strong> must be registered <strong>before</strong> running this method.
     * 
     * @see #register(ConfigEntry, Field, String)
     */
    public void buildDependencies() {
        registry.values().stream()
                .filter(EntryRecord::hasDependencies)
                .forEach(entry -> {
                    // Combine the dependencies, then add the result to the config entry
                    
                    // "enable if" dependencies:
                    Optional.ofNullable(combineDependencies(entry.enableIfDependencies(), entry.enableIfGroups()))
                            .ifPresent(entry.gui()::setEnableIfDependency);
                    
                    // "show if" dependencies:
                    Optional.ofNullable(combineDependencies(entry.showIfDependencies(), entry.showIfGroups()))
                            .ifPresent(entry.gui()::setShowIfDependency);
                });
    }
    
    /**
     * <ul>
     *     <li>If only one dependency is provided, that dependency is built and returned.</li>
     *     <li>If multiple dependencies are provided, they are combined into a {@link DependencyGroup} requiring all of them.</li>
     * </ul>
     * 
     * @param dependencies a {@link Collection} of "enable if" {@link DependencyDefinition}s
     * @param dependencyGroups a {@link Collection} of "enable if" {@link DependencyGroupDefinition}s
     * @return a single {@link Dependency} or {@link DependencyGroup} representing all dependencies provided,
     * or {@code null} if {@code dependencies} is empty
     */
    private @Nullable Dependency combineDependencies(@NotNull Collection<DependencyDefinition> dependencies, @NotNull Collection<DependencyGroupDefinition> dependencyGroups)
    {
        // Build each dependency
        List<Dependency> singles = dependencies.stream()
                .map(this::buildDependency)
                .toList();
        List<Dependency> groups = dependencyGroups.stream()
                .map(this::buildDependency)
                .collect(Collectors.toCollection(ArrayList::new));
    
        // Combine multiple dependencies if necessary,
        // add the result to the groups list
        if (!singles.isEmpty())
            groups.add(singles.size() == 1 ? singles.get(0) : Dependency.groupBuilder().withChildren(singles).build());
    
        // Filter duplicates before checking quantities
        List<Dependency> children = groups.stream().distinct().toList();
        
        // Don't build a group if we only have one dependency
        if (children.isEmpty())
            return null;
        else if (children.size() == 1)
            return children.get(0);
        
        // Return a group that depends on all dependencies & groups
        // Filtered to remove any duplicates
        return Dependency.groupBuilder()
                .withChildren(children)
                .build();
    }
    
    /**
     * Build a {@link DependencyGroup} as defined in the {@link DependencyGroupDefinition definition}.
     * <br><br>
     * If there is an issue building any child dependency, a {@link RuntimeException} will be thrown.
     *
     * @param dependencyGroup The {@link DependencyGroupDefinition} defining the group
     * @return The built {@link DependencyGroup}
     * @throws RuntimeException when there is an issue building one of the group's dependencies
     */
    public Dependency buildDependency(DependencyGroupDefinition dependencyGroup) {
        // Build each child DependencyDefinition
        Set<Dependency> dependencies = dependencyGroup.buildChildren(this::buildDependency);
        
        // If there's only one child, don't bother making a group
        // unless the group is being used to invert the child
        if (dependencies.size() == 1) {
            boolean invert = switch (dependencyGroup.condition()) {
                case ALL, ANY, ONE -> dependencyGroup.inverted();
                case NONE -> !dependencyGroup.inverted();
            };
            if (!invert)
                return dependencies.iterator().next();
        }
    
        // Build and return the DependencyGroup
        return Dependency.groupBuilder()
                .inverted(dependencyGroup.inverted())
                .withCondition(dependencyGroup.condition())
                .withChildren(dependencies)
                .build();
    }
    
    /**
     * Build a {@link Dependency} as defined in the {@link DependencyDefinition definition}.
     * <br><br>
     * <p>Currently, supports depending on the following:
     * <ul>
     *     <li>{@link BooleanDependency} from {@link BooleanListEntry}</li>
     *     <li>{@link EnumDependency} from {@link EnumListEntry}</li>
     *     <li>{@link NumberDependency} from entries implementing {@link NumberConfigEntry}</li>
     * </ul>
     * <p>If a different config entry type is used, a {@link RuntimeException} will be thrown.
     *
     * @param dependency The {@link DependencyDefinition} defining the dependency
     * @return The built {@link Dependency}
     * @throws IllegalArgumentException if the defined dependency is invalid
     */
    public Dependency buildDependency(DependencyDefinition dependency) {
        ConfigEntry<?> gui = getEntry(dependency.i18n())
                .orElseThrow(() -> new IllegalArgumentException("Specified dependency not found: \"%s\"".formatted(dependency.i18n())));
        
        if (gui instanceof BooleanListEntry booleanListEntry)
            return buildDependency(dependency, booleanListEntry);
        else if (gui instanceof EnumListEntry<?> selectionListEntry)
            return buildDependency(dependency, selectionListEntry);
        else if (gui instanceof NumberConfigEntry<?> numberConfigEntry)
            return buildDependency(dependency, numberConfigEntry);
        else
            throw new IllegalArgumentException("Unsupported dependency type: %s".formatted(gui.getClass().getSimpleName()));
    }
    
    /**
     * Builds a {@link BooleanDependency} defined in the {@link DependencyDefinition}, depending on the given {@link BooleanListEntry}.
     * 
     * @param dependency the {@link DependencyDefinition} that defines the dependency
     * @param gui the {@link BooleanListEntry} to be depended on
     * @return the generated dependency
     */
    public BooleanDependency buildDependency(DependencyDefinition dependency, BooleanListEntry gui) {
        Set<BooleanCondition> conditions = dependency.buildConditions(StaticConditionDefinition::toBooleanCondition);
        Set<ConfigEntryMatcher<Boolean>> matchers = dependency.buildMatchers(Boolean.class, this::getEntry);
    
        // Start building the dependency
        BooleanDependencyBuilder builder = Dependency.builder(gui);
        
        // BooleanDependencyBuilder supports zero or one condition being set 
        if (!conditions.isEmpty()) {
            if (conditions.size() != 1)
                throw new IllegalArgumentException("Boolean dependencies require exactly one condition, found " + conditions.size());
    
            conditions.forEach(builder::withCondition);
        }
        
        builder.matching(matchers);         
        return builder.build();
    }
    
    /**
     * Builds a {@link EnumDependency} defined in the {@link DependencyDefinition}, depending on the given {@link EnumListEntry}.
     *
     * @param dependency the {@link DependencyDefinition} that defines the dependency
     * @param gui the {@link EnumListEntry} to be depended on
     * @return the generated dependency
     */
    public <T extends Enum<?>> EnumDependency<T> buildDependency(DependencyDefinition dependency, EnumListEntry<T> gui) {
        Class<T> type = gui.getType();
        Set<EnumCondition<T>> conditions = dependency.buildConditions(condition -> condition.toEnumCondition(type));
        Set<ConfigEntryMatcher<T>> matchers = dependency.buildMatchers(type, this::getEntry);
    
        return Dependency.builder(gui)
                .withConditions(conditions)
                .matching(matchers)
                .build();
    }
    
    /**
     * Builds a {@link NumberDependency} defined in the {@link DependencyDefinition}, depending on the given {@link NumberConfigEntry}.
     *
     * @param dependency the {@link DependencyDefinition} that defines the dependency
     * @param gui the {@link NumberConfigEntry} to be depended on
     * @return the generated dependency
     */
    public <T extends Number & Comparable<T>> NumberDependency<T> buildDependency(DependencyDefinition dependency, NumberConfigEntry<T> gui) {
        Class<T> type = gui.getType();
        Set<NumberCondition<T>> conditions = dependency.buildConditions(condition -> condition.toNumberCondition(type));
        Set<ConfigEntryMatcher<T>> matchers = dependency.buildComparableMatchers(type, this::getEntry);
    
        return Dependency.builder(gui)
                .withConditions(conditions)
                .matching(matchers)
                .build();
    }
    
    /**
     * Return true if a supported dependency annotation is <em>present</em> on the provided field.
     *
     * @param field the field to check
     * @return whether the provided field has dependency annotations present
     */
    public static boolean hasDependencyAnnotation(Field field) {
        return field.isAnnotationPresent(EnableIf.class)
               || field.isAnnotationPresent(EnableIfGroup.class)
               || field.isAnnotationPresent(ShowIf.class)
               || field.isAnnotationPresent(ShowIfGroup.class);
    }
    
    /**
     * Gets the targeted i18n key, using {@code i18nBase} as a <em>base reference</em> if {@code  i18nKey} is relative.
     * 
     * @param i18nBase an absolute i18n key, to be used as the base reference point of the relative key
     * @param i18nKey either a relative or absolute i18n key
     * @return the absolute i18n key
     * @see EnableIf#value() Public API documentation
     */
    static String parseRelativeI18n(@Nullable String i18nBase, String i18nKey) {
        // Count how many "steps up" are at the start of the key string,
        int steps = 0;
        for (char c : i18nKey.toCharArray()) {
            if (STEP_UP_PREFIX == c) steps++;
            else break;
        }
        
        // Not a relative key
        if (steps < 1)
            return i18nKey;
        
        if (i18nBase == null)
            throw new IllegalArgumentException("Relative i18n key cannot be used without a base-reference");
        
        // Get the key without any "step" chars
        String key = i18nKey.substring(steps);
        String base = i18nBase;
        
        // Move `base` up one level for each "step" that was counted
        // Start from 1 since the first "step" is just indicating that the key is relative
        for (int i = 1; i < steps; i++) {
            base = i18nParent(base);
            if (base == null)
                throw new IllegalArgumentException("Too many steps up (%d) relative to \"%s\"".formatted(steps, i18nBase));
        }
        
        return base + I18N_JOINER + key;
    }
    
    /**
     * Gets the parent of the provided i18n key. For example <em>{@code "a.good.child"}</em> returns <em>{@code "a.good"}</em>. 
     * @param i18n the key to get the parent of
     * @return the parent of {@code i18n}
     */
    private static String i18nParent(String i18n) {
        int index = i18n.lastIndexOf(I18N_JOINER);
        
        // No parent to be found
        if (index < 1)
            return null;
        
        return i18n.substring(0, index);
    }
}
