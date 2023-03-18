package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.dependencies.BooleanDependency;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.DependencyGroup;
import me.shedaniel.clothconfig2.api.dependencies.SelectionDependency;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
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
    
    private @Nullable String prefix;
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    private record EntryRecord(String i18n, Field field, AbstractConfigListEntry<?> gui) {}
    private final Map<String, EntryRecord> registry = new LinkedHashMap<>();
    private final Map<String, List<Annotation>> additionalDependencies = new LinkedHashMap<>();
    
    public DependencyManager() {}
    
    /**
     * Register a new or transformed config entry for later use by {@link #buildDependencies()}.
     *
     * @param entry the config entry GUI
     * @param field the field where the config entry was defined
     * @see #buildDependencies()
     */
    public void register(AbstractConfigListEntry<?> entry, Field field) {
        String key = entry.getFieldKey();
        registry.put(key, new EntryRecord(key, field, entry));
    }
    
    /**
     * Register an additional dependency annotation which will be required in addition to any existing dependencies
     * associated with the config entry.
     *
     * @param entry        the entry to add dependencies to
     * @param dependencies one or more {@link ConfigEntry.Gui.DependsOn @DependsOn} or
     *                   {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup} annotations.
     */
    public void registerAdditionalDependency(AbstractConfigListEntry<?> entry, Annotation... dependencies) {
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
        List<Annotation> registry = additionalDependencies.getOrDefault(key, new ArrayList<>());
        Collections.addAll(registry, dependencies);
        additionalDependencies.put(key, registry);
    }
    
    /**
     * Get the config entry GUI associated with the given i18n key.
     * 
     * @param i18n the i18n key 
     * @return An {@link Optional} containing config entry or {@code Optional.empty()}
     */
    public Optional<AbstractConfigListEntry<?>> getEntry(String i18n) {
        return Optional.ofNullable(registry.get(i18n)).map(EntryRecord::gui);
    }
    
    /**
     * Get the field associated with the given i18n key.
     *
     * @param i18n the i18n key 
     * @return An {@link Optional} containing defining field or {@code Optional.empty()}
     */
    public Optional<Field> getField(String i18n) {
        return Optional.ofNullable(registry.get(i18n)).map(EntryRecord::field);
    }
    
    /**
     * Builds the dependencies for all registered config entries.
     * <br><br>
     * Both the dependent and depended-on entry for each dependency must be registered before running this method.
     */
    public void buildDependencies() {
        registry.values().stream()
                // FIXME is this filter really needed?
                //  The forEach handler already checks whether any dependencies are present,
                //  so this is only helpful if it actually speeds things up...
                .filter(record ->
                           additionalDependencies.get(record.i18n()) != null
                        || record.field().isAnnotationPresent(ConfigEntry.Gui.DependsOn.class)
                        || record.field().isAnnotationPresent(ConfigEntry.Gui.DependsOnGroup.class))
                .forEach(record -> {
                    String i18n = record.i18n();
                    Field field = record.field();
                    AbstractConfigListEntry<?> gui = record.gui();
                    
                    // Build a dependency annotation list,
                    // populated with any dependencies declared on the field
                    // or registered in the additionalDependencies map
                    List<Annotation> dependencies = new ArrayList<>();
                    if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOnGroup.class))
                        dependencies.add(field.getAnnotation(ConfigEntry.Gui.DependsOnGroup.class));
                    if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOn.class))
                        dependencies.add(field.getAnnotation(ConfigEntry.Gui.DependsOn.class));
                    Optional.ofNullable(additionalDependencies.get(i18n))
                            .ifPresent(dependencies::addAll);
                    
                    if (dependencies.isEmpty())
                        return;
                    
                    // Combine the dependencies,
                    // then add the result to the config entry
                    Optional.ofNullable(combineDependencies(dependencies))
                            .ifPresent(gui::setDependency);
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
        
        // FIXME remove duplicate dependencies
    
        // Build all single Dependencies
        List<Dependency> singles = dependencies.stream()
                .filter(ConfigEntry.Gui.DependsOn.class::isInstance)
                .map(ConfigEntry.Gui.DependsOn.class::cast)
                .map(this::buildDependency)
                .toList();

        // Build all DependencyGroups
        List<Dependency> groups = dependencies.stream()
                .filter(ConfigEntry.Gui.DependsOnGroup.class::isInstance)
                .map(ConfigEntry.Gui.DependsOnGroup.class::cast)
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
            groups.add(singles.size() == 1 ?
                    singles.get(0) : Dependency.all(singles));
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
     * Currently, supports {@link BooleanListEntry} and {@link SelectionListEntry} dependencies.
     * If a different config entry type is used, a {@link RuntimeException} will be thrown.
     *
     * @param annotation The {@link ConfigEntry.Gui.DependsOn @DependsOn} annotation defining the dependency
     * @return The built {@link Dependency}
     * @throws RuntimeException when an unsupported dependency type is used, or the annotation is somehow invalid
     */
    public Dependency buildDependency(ConfigEntry.Gui.DependsOn annotation) {
        String i18n = prefix != null && annotation.value().startsWith(prefix) ?
                annotation.value() : "%s.%s".formatted(prefix, annotation.value());
    
        AbstractConfigListEntry<?> dependency = getEntry(i18n)
                .orElseThrow(() -> new RuntimeException("Specified dependency not found: \"%s\"".formatted(i18n)));
    
        if (dependency instanceof BooleanListEntry booleanListEntry)
            return buildDependency(annotation, booleanListEntry);
        else if (dependency instanceof SelectionListEntry<?> selectionListEntry)
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
        List<Boolean> conditions = Arrays.stream(annotation.conditions())
                // Functionally equivalent to Boolean::parseBoolean, but allows us to throw a RuntimeException
                .map(condition -> switch (condition.toLowerCase()) {
                    case "true" -> true;
                    case "false" -> false;
                    default -> throw new IllegalStateException("Unexpected condition \"%s\" for Boolean dependency (expected \"true\" or \"false\").".formatted(condition));
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
    public static <T> SelectionDependency<T> buildDependency(ConfigEntry.Gui.DependsOn annotation, SelectionListEntry<T> dependency) {
        // List of valid values for the depended-on SelectionListEntry
        List<T> possibleValues = dependency.getValues();
    
        // Convert each condition to the appropriate type, by
        // mapping the dependency conditions to matched possible values
        List<T> conditions = Arrays.stream(annotation.conditions())
                .map(condition -> possibleValues.stream()
                        .filter(value -> value.toString().equalsIgnoreCase(condition))
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("Invalid SelectionDependency condition was defined: \"%s\"\nValid options: %s".formatted(condition, possibleValues))))
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
}
