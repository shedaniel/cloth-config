package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.dependencies.BooleanDependency;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.DependencyGroup;
import me.shedaniel.clothconfig2.api.dependencies.SelectionDependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@code DependencyManager} is used by {@link me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess GuiRegistryAccess} implementations
 * to handle generating dependencies defined in {@link ConfigEntry.Gui.DependsOn @DependsOn} and {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup}
 * annotations.
 * <br><br>
 * It is necessary to register <strong>all</strong> config entry GUIs with the {@code DependencyManager} instance before building any dependencies. 
 */
public class DependencyManager {
    
    private record FlaggedCondition(EnumSet<Condition.Flag> flags, String condition) {}
    private record EntryRecord(AbstractConfigListEntry<?> gui, List<Annotation> dependencies) {}
    
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
    public Optional<AbstractConfigListEntry<?>> getEntry(String i18n) {
        String key = prefix != null && i18n.startsWith(prefix) ?
                i18n : "%s.%s".formatted(prefix, i18n);
        return Optional.ofNullable(registry.get(key)).map(EntryRecord::gui);
    }
    
    /**
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     *
     * @param entry the config entry GUI
     * @param field the field where the config entry was defined
     * @see #buildDependencies()
     */
    public void register(AbstractConfigListEntry<?> entry, Field field) {
        String key = entry.getFieldKey();
    
        // Merge already registered dependencies with any declared on the field
        List<Annotation> dependencies = Optional.ofNullable(registry.get(key))
                .map(EntryRecord::dependencies)
                .orElseGet(ArrayList::new);
        if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOn.class))
            dependencies.add(field.getAnnotation(ConfigEntry.Gui.DependsOn.class));
        if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOnGroup.class))
            dependencies.add(field.getAnnotation(ConfigEntry.Gui.DependsOnGroup.class));

        registry.put(key, new EntryRecord(entry, dependencies));
    }
    
    /**
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     * <br><br>
     * Explicitly provide one or more {@link ConfigEntry.Gui.DependsOn @DependsOn} or {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup}
     * annotations to be later applied to this entry.
     *
     * @param entry        the config entry GUI
     * @param dependencies one or more {@link ConfigEntry.Gui.DependsOn @DependsOn} or
     *                   {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup} annotations.
     * @throws IllegalArgumentException if {@code dependencies} contains an Annotation that isn't either 
     *                     {@link ConfigEntry.Gui.DependsOn @DependsOn} or {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup}.
     * @see #buildDependencies()
     */
    public void register(AbstractConfigListEntry<?> entry, Annotation... dependencies) {
        List<String> invalid = Arrays.stream(dependencies)
                .filter(annotation -> !(annotation instanceof ConfigEntry.Gui.DependsOn || annotation instanceof ConfigEntry.Gui.DependsOnGroup))
                .map(Annotation::annotationType)
                .map(Class::getSimpleName)
                .toList();
        if (!invalid.isEmpty())
            throw new IllegalArgumentException(invalid.size() == 1 ?
                    "Invalid annotation type \"%s\" passed to registerAdditionalDependency()".formatted(invalid.get(0))
                    : "%d invalid annotations passed to registerAdditionalDependency(): %s".formatted(invalid.size(), invalid));
    
        String key = entry.getFieldKey();
    
        // Merge new & existing dependencies
        List<Annotation> dependenciesList = Optional.ofNullable(registry.get(key))
                .map(EntryRecord::dependencies)
                .orElseGet(ArrayList::new);
        Collections.addAll(dependenciesList, dependencies);
        
        registry.put(key, new EntryRecord(entry, dependenciesList));
    }
    
    /**
     * Builds the dependencies for all registered config entries.
     * <br><br>
     * Both the dependent and depended-on entry for each dependency must be registered before running this method.
     */
    public void buildDependencies() {
        registry.values().stream()
                .filter(record -> !record.dependencies().isEmpty())
                .forEach(record -> {
                    // Combine the dependencies,
                    // then add the result to the config entry
                    Optional.ofNullable(combineDependencies(record.dependencies()))
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
     * @param dependencies a {@link Collection} containing {@link ConfigEntry.Gui.DependsOn @DependsOn} and {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup}
     *                     annotations
     * @return a single {@link Dependency} or {@link DependencyGroup} representing all dependencies provided,
     *                     or {@code null} if {@code dependencies} is empty
     * @throws IllegalArgumentException if {@code dependencies} contains an Annotation that isn't either 
     *                     {@link ConfigEntry.Gui.DependsOn @DependsOn} or {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup}.
     */
    private @Nullable Dependency combineDependencies(Collection<Annotation> dependencies) {
        // Check for invalid arguments
        List<String> invalid = dependencies.stream()
                .filter(annotation -> !(annotation instanceof ConfigEntry.Gui.DependsOn || annotation instanceof ConfigEntry.Gui.DependsOnGroup))
                .map(Annotation::annotationType)
                .map(Class::getSimpleName)
                .toList();
        if (!invalid.isEmpty())
            throw new IllegalArgumentException(invalid.size() == 1 ?
                    "Invalid annotation type \"%s\" passed to combineDependencies()".formatted(invalid.get(0))
                    : "%d invalid annotations passed to combineDependencies(): %s".formatted(invalid.size(), invalid));
        
        // Build all single Dependencies
        List<Dependency> singles = dependencies.stream()
                .filter(ConfigEntry.Gui.DependsOn.class::isInstance)
                .map(ConfigEntry.Gui.DependsOn.class::cast)
                .distinct()
                .map(this::buildDependency)
                .toList();

        // Build all DependencyGroups
        List<Dependency> groups = dependencies.stream()
                .filter(ConfigEntry.Gui.DependsOnGroup.class::isInstance)
                .map(ConfigEntry.Gui.DependsOnGroup.class::cast)
                .distinct()
                .map(this::buildDependency)
                .collect(Collectors.toCollection(ArrayList::new));
    
        // If we only have one dependency, return it now
        if (singles.isEmpty() && groups.isEmpty())
            return null;
        else if (groups.isEmpty() && singles.size() == 1)
            return singles.get(0);
        else if (singles.isEmpty() && groups.size() == 1)
            return groups.get(0);
    
        // Combine multiple dependencies if necessary,
        // add the result to the groups list
        if (!singles.isEmpty()) {
            Dependency dependency = singles.size() == 1 ?
                    singles.get(0) : Dependency.all(singles);
            // Ensure we don't introduce any duplicates
            if (groups.stream().noneMatch(dependency::equals))
                groups.add(dependency);
        }
        
        // Return a group that depends on all dependencies & groups
        return Dependency.all(groups);
    }
    
    /**
     * Build a {@link DependencyGroup} as defined in the annotation.
     * <br><br>
     * If there is an issue building any child dependency, a {@link RuntimeException} will be thrown.
     *
     * @param annotation The {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup} annotation defining the group
     * @return The built {@link DependencyGroup}
     * @throws RuntimeException when there is an issue building one of the group's dependencies
     */
    public DependencyGroup buildDependency(ConfigEntry.Gui.DependsOnGroup annotation) {
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
     * @param annotation The {@link ConfigEntry.Gui.DependsOn @DependsOn} annotation defining the dependency
     * @return The built {@link Dependency}
     * @throws RuntimeException when an unsupported dependency type is used, or the annotation is somehow invalid
     */
    public Dependency buildDependency(ConfigEntry.Gui.DependsOn annotation) {
        String key = annotation.value();
        AbstractConfigListEntry<?> dependency = getEntry(key)
                .orElseThrow(() -> new RuntimeException("Specified dependency not found: \"%s\"".formatted(key)));
    
        if (dependency instanceof BooleanListEntry booleanListEntry)
            return buildDependency(annotation, booleanListEntry);
        else if (dependency instanceof EnumListEntry<?> selectionListEntry)
            return buildDependency(annotation, selectionListEntry);
        else
            throw new RuntimeException("Unsupported dependency type: %s".formatted(dependency.getClass().getSimpleName()));
    }
    
    /**
     * Builds a {@link BooleanDependency} defined in the {@link ConfigEntry.Gui.DependsOn @DependsOn} annotation, depending on the given {@link BooleanListEntry}.
     * 
     * @param annotation the {@link ConfigEntry.Gui.DependsOn @DependsOn} annotation that defines the dependency
     * @param dependency the {@link BooleanListEntry} to be depended on
     * @return the generated dependency
     */
    public static BooleanDependency buildDependency(ConfigEntry.Gui.DependsOn annotation, BooleanListEntry dependency) {
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
     * Builds a {@link SelectionDependency} defined in the {@link ConfigEntry.Gui.DependsOn @DependsOn} annotation, depending on the given {@link SelectionListEntry}.
     *
     * @param annotation the {@link ConfigEntry.Gui.DependsOn @DependsOn} annotation that defines the dependency
     * @param dependency the {@link SelectionListEntry} to be depended on
     * @return the generated dependency
     */
    public static <T extends Enum<?>> SelectionDependency<T> buildDependency(ConfigEntry.Gui.DependsOn annotation, EnumListEntry<T> dependency) {
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
        SelectionDependency<T> selectionDependency = Dependency.disabledWhenNotMet(dependency, conditions.get(0));
        if (conditions.size() > 1)
            selectionDependency.addConditions(conditions.subList(1, conditions.size()));
        selectionDependency.hiddenWhenNotMet(annotation.hiddenWhenNotMet());
    
        return selectionDependency;
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
