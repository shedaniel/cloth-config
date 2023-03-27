package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnableIf;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnableIfGroup;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.ShowIf;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.ShowIfGroup;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
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
 * annotations.
 * <br><br>
 * It is necessary to register <strong>all</strong> config entry GUIs with the {@code DependencyManager} instance before building any dependencies.
 *
 * @see EnableIf @EnableIf
 * @see EnableIfGroup @EnableIfGroup
 * @see ShowIf @ShowIf
 * @see ShowIfGroup @ShowIfGroup
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
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     *
     * @param entry     the config entry GUI
     * @param field     a {@link Field} with dependency annotations present
     * @param fieldI18n the i18n key of the field (not the GUI)
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
        
        String baseI18n = Optional.ofNullable(fieldI18n)
                .map(DependencyManager::i18nParent)
                .orElse(null);
    
        register(entry, enableIfs, enableIfGroups, showIfs, showIfGroups, baseI18n);
    }
    
    /**
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     * <br><br>
     * Explicitly provide one or more {@link EnableIf @EnableIf} or {@link EnableIfGroup @EnableIfGroup}
     * annotations to be later applied to this entry.
     *
     * @param entry            the config entry GUI
     * @param enableIfs     a {@link Collection} of {@link EnableIf @EnableIf} annotations
     * @param enableIfGroups a {@link Collection} of {@link EnableIfGroup @EnableIfGroup} annotations
     * @param baseI18n         an absolute i18n key, to be used as the base reference of the dependencies
     * @see #buildDependencies()
     */
    public void register(
            ConfigEntry<?> entry,
            @Nullable Collection<EnableIf> enableIfs,
            @Nullable Collection<EnableIfGroup> enableIfGroups,
            @Nullable Collection<ShowIf> showIfs,
            @Nullable Collection<ShowIfGroup> showIfGroups,
            @Nullable String baseI18n)
    {
        String key = entry.getI18nKey();
    
        // Get the already defined sets if they exist
        Optional<EntryRecord> optional = Optional.ofNullable(registry.get(key));
        Set<DependencyRecord> enableIfRecords = optional
                .map(EntryRecord::enableIfDependencies)
                .orElseGet(LinkedHashSet::new);
        Set<DependencyGroupRecord> enableIfGroupRecords = optional
                .map(EntryRecord::enableIfGroups)
                .orElseGet(LinkedHashSet::new);
        Set<DependencyRecord> showIfRecords = optional
                .map(EntryRecord::showIfDependencies)
                .orElseGet(LinkedHashSet::new);
        Set<DependencyGroupRecord> showIfGroupRecords = optional
                .map(EntryRecord::showIfGroups)
                .orElseGet(LinkedHashSet::new);
    
        // Add the new dependencies to the appropriate sets
        enableIfRecords.addAll(Stream.ofNullable(enableIfs)
                .flatMap(Collection::stream)
                .map(single -> new DependencyRecord(baseI18n, single))
                .collect(Collectors.toUnmodifiableSet()));
    
        enableIfGroupRecords.addAll(Stream.ofNullable(enableIfGroups)
                .flatMap(Collection::stream)
                .map(group -> new DependencyGroupRecord(baseI18n, group))
                .collect(Collectors.toUnmodifiableSet()));
        
        showIfRecords.addAll(Stream.ofNullable(showIfs)
                .flatMap(Collection::stream)
                .map(single -> new DependencyRecord(baseI18n, single))
                .collect(Collectors.toUnmodifiableSet()));
    
        showIfGroupRecords.addAll(Stream.ofNullable(showIfGroups)
                .flatMap(Collection::stream)
                .map(group -> new DependencyGroupRecord(baseI18n, group))
                .collect(Collectors.toUnmodifiableSet()));
        
        registry.put(key, new EntryRecord(entry, enableIfRecords, enableIfGroupRecords, showIfRecords, showIfGroupRecords));
    }
    
    /**
     * Builds the dependencies for all registered config entries.
     * <br><br>
     * Both the dependent and depended-on entry for each dependency must be registered before running this method.
     */
    public void buildDependencies() {
        registry.values().stream()
                .filter(EntryRecord::hasDependencies)
                .forEach(record -> {
                    // Combine the dependencies, then add the result to the config entry
                    
                    // "enable if" dependencies:
                    Optional.ofNullable(combineDependencies(record.enableIfDependencies(), record.enableIfGroups()))
                            .ifPresent(record.gui()::setEnableIfDependency);
                    
                    // "show if" dependencies:
                    Optional.ofNullable(combineDependencies(record.showIfDependencies(), record.showIfGroups()))
                            .ifPresent(record.gui()::setShowIfDependency);
                });
    }
    
    /**
     * If only one dependency annotation is provided, that dependency is built and returned.
     * If multiple dependencies are provided, they are combined into a {@link DependencyGroup} using {@link Dependency#all Dependency.all()}.
     * <br><br>
     * Note: a {@link List} containing only one valid dependency annotation is treated as providing only one dependency,
     * provided no other dependencies are passed in explicitly.
     *
     * @param dependencies a {@link Collection} of "enable if" {@link DependencyRecord}s
     * @param dependencyGroups a {@link Collection} of "enable if" {@link DependencyGroupRecord}s
     * @return a single {@link Dependency} or {@link DependencyGroup} representing all dependencies provided,
     * or {@code null} if {@code dependencies} is empty
     */
    private @Nullable Dependency combineDependencies(@NotNull Collection<DependencyRecord> dependencies, @NotNull Collection<DependencyGroupRecord> dependencyGroups)
    {
        // Build each annotation
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
     * Build a {@link DependencyGroup} as defined in the annotation.
     * <br><br>
     * If there is an issue building any child dependency, a {@link RuntimeException} will be thrown.
     *
     * @param dependencyGroup The {@link DependencyGroupRecord} defining the group
     * @return The built {@link DependencyGroup}
     * @throws RuntimeException when there is an issue building one of the group's dependencies
     */
    public Dependency buildDependency(DependencyGroupRecord dependencyGroup) {
        // Build each dependency as defined in DependsOn annotations
        Dependency[] dependencies = Arrays.stream(dependencyGroup.children())
                .map(this::buildDependency)
                .distinct()
                .toArray(Dependency[]::new);
        
        // If there's only one child, don't bother making a group
        // unless the group is being used to invert the child
        if (dependencies.length == 1) {
            boolean invert = switch (dependencyGroup.condition()) {
                case ALL, ANY, ONE -> dependencyGroup.inverted();
                case NONE -> !dependencyGroup.inverted();
            };
            if (!invert)
                return dependencies[0];
        }
    
        // Build and return the DependencyGroup
        return Dependency.groupBuilder()
                .inverted(dependencyGroup.inverted())
                .withCondition(dependencyGroup.condition())
                .withChildren(dependencies)
                .build();
    }
    
    /**
     * Build a {@link Dependency} as defined in the annotation.
     * <br><br>
     * <p>Currently, supports depending on the following:
     * <ul>
     *     <li>{@link BooleanDependency} from {@link BooleanListEntry}</li>
     *     <li>{@link EnumDependency} from {@link EnumListEntry}</li>
     *     <li>{@link NumberDependency} from entries implementing {@link NumberConfigEntry}</li>
     * </ul>
     * <p>If a different config entry type is used, a {@link RuntimeException} will be thrown.
     *
     * @param dependency The {@link DependencyRecord} annotation defining the dependency
     * @return The built {@link Dependency}
     * @throws IllegalArgumentException if the defined dependency is invalid
     */
    public Dependency buildDependency(DependencyRecord dependency) {
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
     * Builds a {@link BooleanDependency} defined in the {@link DependencyRecord}, depending on the given {@link BooleanListEntry}.
     * 
     * @param dependency the {@link DependencyRecord} that defines the dependency
     * @param gui the {@link BooleanListEntry} to be depended on
     * @return the generated dependency
     */
    public static BooleanDependency buildDependency(DependencyRecord dependency, BooleanListEntry gui) {
        List<BooleanCondition> conditions = Arrays.stream(dependency.conditions())
                .map(BooleanCondition::fromConditionString)
                .toList();
        
        // Start building the dependency
        BooleanDependencyBuilder builder = Dependency.builder(gui);
        
        // BooleanDependencyBuilder supports zero or one condition being set 
        if (!conditions.isEmpty()) {
            if (conditions.size() != 1)
                throw new IllegalArgumentException("Boolean dependencies require exactly one condition, found " + conditions.size());
            
            builder.withCondition(conditions.get(0));
        }
            
        return builder.build();
    }
    
    /**
     * Builds a {@link EnumDependency} defined in the {@link DependencyRecord}, depending on the given {@link EnumListEntry}.
     *
     * @param dependency the {@link DependencyRecord} that defines the dependency
     * @param gui the {@link EnumListEntry} to be depended on
     * @return the generated dependency
     */
    public static <T extends Enum<?>> EnumDependency<T> buildDependency(DependencyRecord dependency, EnumListEntry<T> gui) {
        Class<T> type = gui.getType();
        Set<EnumCondition<T>> conditions = Arrays.stream(dependency.conditions())
                .map(condition -> EnumCondition.fromConditionString(type, condition))
                .collect(Collectors.toUnmodifiableSet());
    
        return Dependency.builder(gui)
                .withConditions(conditions)
                .build();
    }
    
    /**
     * Builds a {@link NumberDependency} defined in the {@link DependencyRecord}, depending on the given {@link NumberConfigEntry}.
     *
     * @param dependency the {@link DependencyRecord} that defines the dependency
     * @param gui the {@link NumberConfigEntry} to be depended on
     * @return the generated dependency
     */
    public static <T extends Number & Comparable<T>> NumberDependency<T> buildDependency(DependencyRecord dependency, NumberConfigEntry<T> gui) {
        Class<T> type = gui.getType();
        Set<NumberCondition<T>> conditions = Arrays.stream(dependency.conditions())
                .map(condition -> NumberCondition.fromConditionString(type, condition))
                .collect(Collectors.toUnmodifiableSet());
    
        return Dependency.builder(gui)
                .withConditions(conditions)
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
    private static String parseRelativeI18n(String i18nBase, String i18nKey) {
        // Count how many "steps up" are at the start of the key string,
        int steps = 0;
        for (char c : i18nKey.toCharArray()) {
            if (STEP_UP_PREFIX == c) steps++;
            else break;
        }
        
        // Not a relative key
        if (steps < 1) return i18nKey;
        
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
    
    private record DependencyRecord(String i18n, String[] conditions) {
        private DependencyRecord(String baseI18n, EnableIf annotation) {
            this(baseI18n, annotation.value(), annotation.conditions());
        }
        private DependencyRecord(String baseI18n, ShowIf annotation) {
            this(baseI18n, annotation.value(), annotation.conditions());
        }
        public DependencyRecord(String baseI18n, String i18n, String[] conditions) {
            this(parseRelativeI18n(baseI18n, i18n), conditions);
        }
    }
    
    private record DependencyGroupRecord(DependencyGroup.Condition condition, boolean inverted, DependencyRecord[] children) {
        private DependencyGroupRecord(String baseI18n, EnableIfGroup annotation) {
            this(annotation.condition(), annotation.inverted(),
                    Arrays.stream(annotation.value())
                            .map(child -> new DependencyRecord(baseI18n, child))
                            .toArray(DependencyRecord[]::new));
        }
        private DependencyGroupRecord(String baseI18n, ShowIfGroup annotation) {
            this(annotation.condition(), annotation.inverted(),
                 Arrays.stream(annotation.value())
                         .map(child -> new DependencyRecord(baseI18n, child))
                         .toArray(DependencyRecord[]::new));
        }
    }
    
    private record EntryRecord(
            ConfigEntry<?> gui,
            Set<DependencyRecord> enableIfDependencies,
            Set<DependencyGroupRecord> enableIfGroups,
            Set<DependencyRecord> showIfDependencies,
            Set<DependencyGroupRecord> showIfGroups)
    {
        boolean hasDependencies() {
            return !(
                        enableIfDependencies().isEmpty() &&
                        enableIfGroups().isEmpty() &&
                        showIfDependencies().isEmpty() &&
                        showIfGroups().isEmpty()
            );
        }
    }
}
