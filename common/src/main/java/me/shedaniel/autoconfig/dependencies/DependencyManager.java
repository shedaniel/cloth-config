package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.dependencies.BooleanDependency;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.DependencyGroup;
import me.shedaniel.clothconfig2.api.dependencies.SelectionDependency;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;

import java.lang.reflect.Field;
import java.util.*;

/**
 * {@code DependencyManager} is used by {@link me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess GuiRegistryAccess} implementations
 * to handle generating dependencies defined in {@link ConfigEntry.Gui.DependsOn @DependsOn} and {@link ConfigEntry.Gui.DependsOnGroup @DependsOnGroup}
 * annotations.
 * <br><br>
 * It is necessary to register <strong>all</strong> config entry GUIs with the {@code DependencyManager} instance before building any dependencies. 
 */
public class DependencyManager {
    
    private record EntryRecord(String i18n, Field field, AbstractConfigListEntry<?> gui) {}
    private final Map<String, EntryRecord> registry = new LinkedHashMap<>();
    
    public DependencyManager() {}
    
    /**
     * Register a new or transformed config entry for later use by {@code buildDependencies}.
     * 
     * @param i18n the i18n key 
     * @param field the field where the config entry was defined
     * @param entry the config entry GUI
     */
    public void register(String i18n, Field field, AbstractConfigListEntry<?> entry) {
        registry.put(i18n, new EntryRecord(i18n, field, entry));
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
                .filter(entry -> entry.field().isAnnotationPresent(ConfigEntry.Gui.DependsOn.class) || entry.field().isAnnotationPresent(ConfigEntry.Gui.DependsOnGroup.class))
                .forEach(entry -> {
                    Field field = entry.field();
                    Dependency dependency;

                    if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOnGroup.class))
                        dependency = buildDependency(field.getAnnotation(ConfigEntry.Gui.DependsOnGroup.class));
                    else if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOn.class))
                        dependency = buildDependency(field.getAnnotation(ConfigEntry.Gui.DependsOn.class));
                    else
                        throw new RuntimeException("Neither DependsOn nor DependsOnGroup annotation is present.");

                    entry.gui().setDependency(dependency);
                });
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
            case ALL -> Dependency.all(dependencies);
            case NONE -> Dependency.none(dependencies);
            case ANY -> Dependency.any(dependencies);
            case ONE -> Dependency.one(dependencies);
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
        String i18n = annotation.value();
    
        AbstractConfigListEntry<?> dependency = getEntry(i18n)
                .orElseThrow(() -> new RuntimeException("Specified dependency not found: \"%s\"".formatted(i18n)));
    
        if (dependency instanceof BooleanListEntry booleanListEntry) {
            return buildDependency(annotation, booleanListEntry);
        } else if (dependency instanceof SelectionListEntry<?> selectionListEntry) {
            return buildDependency(annotation, selectionListEntry);
        } else {
            throw new RuntimeException("Unsupported dependency type: %s".formatted(dependency.getClass().getSimpleName()));
        }
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
