package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.DependsOn;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.DependsOnGroup;
import me.shedaniel.clothconfig2.api.dependencies.*;
import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.NumberCondition;
import me.shedaniel.clothconfig2.api.entries.ConfigEntry;
import me.shedaniel.clothconfig2.api.entries.NumberConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@code DependencyManager} is used by {@link me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess GuiRegistryAccess} implementations
 * to handle generating dependencies defined in {@link DependsOn @DependsOn} and {@link DependsOnGroup @DependsOnGroup}
 * annotations.
 * <br><br>
 * It is necessary to register <strong>all</strong> config entry GUIs with the {@code DependencyManager} instance before building any dependencies. 
 */
public class DependencyManager {
    
    private record FlaggedCondition(EnumSet<Condition.Flag> flags, String condition) {}
    private record EntryRecord(ConfigEntry<?> gui, Set<DependsOn> dependencies, Set<DependsOnGroup> groupDependencies) {
        boolean hasDependencies() {
            return !(dependencies().isEmpty() && groupDependencies().isEmpty());
        }
    }
    
    private static final Character FLAG_PREFIX = '{';
    private static final Character FLAG_SUFFIX = '}';
    
    private final Map<String, EntryRecord> registry = new LinkedHashMap<>();
    
    private @Nullable String prefix;
    
    public DependencyManager() {}
    
    /**
     * Define a prefix which {@link #getEntry(String i18n)} will add
     * to i18n keys if they don't already start with it.
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
                i18n : "%s.%s".formatted(prefix, i18n);
        return Optional.ofNullable(registry.get(key)).map(EntryRecord::gui);
    }
    
    /**
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     *
     * @param entry the config entry GUI
     * @param field a {@link Field} with dependency annotations present
     * @see #buildDependencies()
     */
    public void register(ConfigEntry<?> entry, @Nullable Field field) {
        Collection<DependsOn> singles = null;
        Collection<DependsOnGroup> groups = null;
        
        if (field != null) {
            if (field.isAnnotationPresent(DependsOn.class))
                singles = Collections.singleton(field.getAnnotation(DependsOn.class));
            if (field.isAnnotationPresent(DependsOnGroup.class))
                groups = Collections.singleton(field.getAnnotation(DependsOnGroup.class));
        }

        register(entry, singles, groups);
    }
    
    /**
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     * <br><br>
     * Explicitly provide one or more {@link DependsOn @DependsOn} or {@link DependsOnGroup @DependsOnGroup}
     * annotations to be later applied to this entry.
     *
     * @param entry        the config entry GUI
     * @param dependencies a {@link Collection} of {@link DependsOn @DependsOn} annotations
     * @param groupDependencies a {@link Collection} of {@link DependsOnGroup @DependsOnGroup} annotations
     * @see #buildDependencies()
     */
    public void register(ConfigEntry<?> entry, @Nullable Collection<DependsOn> dependencies, @Nullable Collection<DependsOnGroup> groupDependencies) {
        String key = entry.getI18nKey();
    
        // Get the already defined sets if they exist
        Optional<EntryRecord> record = Optional.ofNullable(registry.get(key));
        Set<DependsOn> singles = record
                .map(EntryRecord::dependencies)
                .orElseGet(LinkedHashSet::new);
        Set<DependsOnGroup> groups = record
                .map(EntryRecord::groupDependencies)
                .orElseGet(LinkedHashSet::new);
    
        // Add any dependency annotations provided
        if (dependencies != null && !dependencies.isEmpty())
            singles.addAll(dependencies);
        if (groupDependencies != null && !groupDependencies.isEmpty())
            groups.addAll(groupDependencies);
        
        registry.put(key, new EntryRecord(entry, singles, groups));
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
                    // Combine the dependencies,
                    // then add the result to the config entry
                    Optional.ofNullable(combineDependencies(record.dependencies(), record.groupDependencies()))
                            .ifPresent(record.gui()::setDependency);
                });
    }
    
    /**
     * If only one dependency annotation is provided, that dependency is built and returned.
     * If multiple dependencies are provided, they are combined into a {@link DependencyGroup} using {@link Dependency#all Dependency.all()}.
     * <br><br>
     * Note: a {@link List} containing only one valid dependency annotation is treated as providing only one dependency,
     * provided no other dependencies are passed in explicitly.
     *
     * @param dependencies a {@link Collection} of {@link DependsOn @DependsOn} annotations
     * @param groupDependencies  a {@link Collection} of {@link DependsOnGroup @DependsOnGroup} annotations
     * @return a single {@link Dependency} or {@link DependencyGroup} representing all dependencies provided,
     *                     or {@code null} if {@code dependencies} is empty
     */
    private @Nullable Dependency combineDependencies(@NotNull Collection<DependsOn> dependencies, @NotNull Collection<DependsOnGroup> groupDependencies) {
        // Build each annotation
        List<Dependency> singles = dependencies.stream()
                .map(this::buildDependency)
                .toList();
        List<Dependency> groups = groupDependencies.stream()
                .map(this::buildDependency)
                .collect(Collectors.toCollection(ArrayList::new));
    
        // Return early if we only have one dependency
        if (singles.isEmpty() && groups.isEmpty())
            return null;
        else if (groups.isEmpty() && singles.size() == 1)
            return singles.get(0);
        else if (singles.isEmpty() && groups.size() == 1)
            return groups.get(0);
    
        // Combine multiple dependencies if necessary,
        // add the result to the groups list
        if (!singles.isEmpty())
            groups.add(singles.size() == 1 ? singles.get(0) : Dependency.all(singles));
        
        // Return a group that depends on all dependencies & groups
        // Filtered to remove any duplicates
        return Dependency.all(groups.stream().distinct().toList());
    }
    
    /**
     * Build a {@link DependencyGroup} as defined in the annotation.
     * <br><br>
     * If there is an issue building any child dependency, a {@link RuntimeException} will be thrown.
     *
     * @param annotation The {@link DependsOnGroup @DependsOnGroup} annotation defining the group
     * @return The built {@link DependencyGroup}
     * @throws RuntimeException when there is an issue building one of the group's dependencies
     */
    public DependencyGroup buildDependency(DependsOnGroup annotation) {
        // Build each dependency as defined in DependsOn annotations
        Dependency[] dependencies = Arrays.stream(annotation.value())
                .map(this::buildDependency)
                .toArray(Dependency[]::new);
        
        // Return the appropriate DependencyGroup variant
        return switch (annotation.condition()) {
            case ALL  -> Dependency.all(dependencies);
            case NONE -> Dependency.none(dependencies);
            case ANY  -> Dependency.any(dependencies);
            case ONE  -> Dependency.one(dependencies);
        };
    }
    
    /**
     * Build a {@link Dependency} as defined in the annotation.
     * <br><br>
     * Currently, supports {@link BooleanListEntry} and {@link EnumListEntry} dependencies.
     * If a different config entry type is used, a {@link RuntimeException} will be thrown.
     *
     * @param annotation The {@link DependsOn @DependsOn} annotation defining the dependency
     * @return The built {@link Dependency}
     * @throws RuntimeException when an unsupported dependency type is used, or the annotation is somehow invalid
     */
    public Dependency buildDependency(DependsOn annotation) {
        String key = annotation.value();
        ConfigEntry<?> dependency = getEntry(key)
                .orElseThrow(() -> new RuntimeException("Specified dependency not found: \"%s\"".formatted(key)));
    
        if (dependency instanceof BooleanListEntry booleanListEntry)
            return buildDependency(annotation, booleanListEntry);
        else if (dependency instanceof EnumListEntry<?> selectionListEntry)
            return buildDependency(annotation, selectionListEntry);
        else if (dependency instanceof NumberConfigEntry<?> numberConfigEntry)
            return buildDependency(annotation, numberConfigEntry);
        else
            throw new RuntimeException("Unsupported dependency type: %s".formatted(dependency.getClass().getSimpleName()));
    }
    
    /**
     * Builds a {@link BooleanDependency} defined in the {@link DependsOn @DependsOn} annotation, depending on the given {@link BooleanListEntry}.
     * 
     * @param annotation the {@link DependsOn @DependsOn} annotation that defines the dependency
     * @param dependency the {@link BooleanListEntry} to be depended on
     * @return the generated dependency
     */
    public static BooleanDependency buildDependency(DependsOn annotation, BooleanListEntry dependency) {
        List<BooleanCondition> conditions = Arrays.stream(annotation.conditions())
                .map(DependencyManager::parseFlags)
                .map(record -> {
                    // The switch expression is functionally equivalent to Boolean::parseBoolean,
                    // but allows us to throw a RuntimeException
                    String string = record.condition().strip().toLowerCase();
                    BooleanCondition condition = new BooleanCondition(switch (string) {
                        case "true" -> true;
                        case "false" -> false;
                        default ->
                                throw new IllegalStateException("Unexpected condition \"%s\" for Boolean dependency (expected \"true\" or \"false\").".formatted(string));
                    });
                    condition.setFlags(record.flags());
                    return condition;
                })
                .toList();
        
        if (conditions.size() != 1)
            throw new IllegalStateException("Boolean dependencies require exactly one condition, found " + conditions.size());
        
        // Finally, build the dependency and return it
        BooleanDependency booleanDependency = Dependency.disabledWhenNotMet(dependency, conditions.get(0));
        booleanDependency.hiddenWhenNotMet(annotation.hiddenWhenNotMet());
        
        return booleanDependency;
    }
    
    
    /**
     * Builds a {@link EnumDependency} defined in the {@link DependsOn @DependsOn} annotation, depending on the given {@link EnumListEntry}.
     *
     * @param annotation the {@link DependsOn @DependsOn} annotation that defines the dependency
     * @param dependency the {@link EnumListEntry} to be depended on
     * @return the generated dependency
     */
    public static <T extends Enum<?>> EnumDependency<T> buildDependency(DependsOn annotation, EnumListEntry<T> dependency) {
        // List of valid values for the depended-on SelectionListEntry
        List<T> possibleValues = dependency.getValues();

        // Convert each condition to the appropriate type, by
        // mapping the dependency conditions to matched possible values
        List<EnumCondition<T>> conditions = Arrays.stream(annotation.conditions())
                .map(DependencyManager::parseFlags)
                .map(record -> {
                    String string = record.condition().strip().toLowerCase();
                    EnumCondition<T> condition = new EnumCondition<>(possibleValues.stream()
                            .filter(val -> val.toString().equalsIgnoreCase(string))
                            .findAny()
                            .orElseThrow(() -> new IllegalStateException("Invalid SelectionDependency condition was defined: \"%s\"\nValid options: %s".formatted(record.condition(), possibleValues))));
                    condition.setFlags(record.flags());
                    return condition;
                })
                .toList();
    
        // Check enough conditions were parsed
        if (conditions.isEmpty())
            throw new IllegalStateException("SelectionList dependency requires at least one condition");
    
        // Finally, build the dependency and return it
        EnumDependency<T> enumDependency = Dependency.disabledWhenNotMet(dependency, conditions.get(0));
        if (conditions.size() > 1)
            enumDependency.addConditions(conditions.subList(1, conditions.size()));
        enumDependency.hiddenWhenNotMet(annotation.hiddenWhenNotMet());
    
        return enumDependency;
    }
    
    public static <T extends Number & Comparable<T>> NumberDependency<T> buildDependency(DependsOn annotation, NumberConfigEntry<T> dependency) {
        Class<T> type = dependency.getType();
        
        List<NumberCondition<T>> conditions = Arrays.stream(annotation.conditions())
                .map(DependencyManager::parseFlags)
                .map(record -> {
                    String string = record.condition().strip().toLowerCase();
                    NumberCondition<T> condition = NumberCondition.fromString(type, string);
                    condition.setFlags(record.flags());
                    return condition;
                })
                .toList();
    
        if (conditions.isEmpty())
            throw new IllegalStateException("Number dependencies require at least one condition.");
    
        NumberDependency<T> numberDependency = Dependency.disabledWhenNotMet(dependency, conditions.get(0));
        if (conditions.size() > 1)
            numberDependency.addConditions(conditions.subList(1, conditions.size()));
        numberDependency.hiddenWhenNotMet(annotation.hiddenWhenNotMet());
        
        return numberDependency;
    }
    
    private static FlaggedCondition parseFlags(String condition) throws IllegalArgumentException {
        if (FLAG_PREFIX == condition.charAt(0)) {
            int flagEnd = condition.indexOf(FLAG_SUFFIX);
            if (flagEnd < 0)
                throw new IllegalArgumentException("Condition \"%s\" starts with the flag prefix '%s', but the flag suffix '%s' was not found. Suggestion: \"%s%s%s\"?"
                        .formatted(condition, FLAG_PREFIX, FLAG_SUFFIX, FLAG_PREFIX, FLAG_SUFFIX, condition));
            
            String flagString = condition.substring(1, flagEnd);
            String conditionString = condition.substring(flagEnd + 1);
            
            return new FlaggedCondition(Condition.Flag.fromString(flagString), conditionString);
        }
        return new FlaggedCondition(Condition.Flag.NONE, condition);
    }
}
