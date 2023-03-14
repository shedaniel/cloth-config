package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.dependencies.BooleanDependency;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.SelectionDependency;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyManager {
    
    private final Map<String, GeneratedConfigEntry> generatedEntries = new HashMap<>();
    
    public DependencyManager() {
        // TODO params?
        System.err.println("new DependencyManager created");
    }
    
    public void register(String i18n, Field field, AbstractConfigListEntry entry) {
        generatedEntries.put(i18n, new GeneratedConfigEntry(i18n, field, entry));
    }
    
    public AbstractConfigListEntry getEntry(String i18n) {
        return generatedEntries.get(i18n).entry;
    }
    
    public Field getField(String i18n) {
        return generatedEntries.get(i18n).field;
    }
    
    public void buildDependencies() {
        generatedEntries.values().stream()
                .filter(entry -> entry.getField().isAnnotationPresent(ConfigEntry.Gui.DependsOn.class) || entry.getField().isAnnotationPresent(ConfigEntry.Gui.DependsOnGroup.class))
                .forEach(entry -> {
                    Field field = entry.getField();
                    Dependency dependency;
                    if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOnGroup.class)) {
                        dependency = buildDependency(field.getAnnotation(ConfigEntry.Gui.DependsOnGroup.class));
                    } else if (field.isAnnotationPresent(ConfigEntry.Gui.DependsOn.class)) {
                        dependency = buildDependency(field.getAnnotation(ConfigEntry.Gui.DependsOn.class));
                    } else {
                        throw new RuntimeException("Neither DependsOn nor DependsOnGroup annotation is present.");
                    }
                    
                    entry.getEntry().setDependency(dependency);
                });
    }
    
    public Dependency buildDependency(ConfigEntry.Gui.DependsOnGroup annotation) {
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
    
    public Dependency buildDependency(ConfigEntry.Gui.DependsOn annotation) {
        String i18n = annotation.value();
        AbstractConfigListEntry<?> dependency = getEntry(i18n);
        if (dependency == null)
            throw new RuntimeException("Specified dependency not found: \"%s\"".formatted(i18n));
    
        return buildDependency(annotation, dependency);
    }
    
    /**
     * Build a {@link Dependency} on the config entry, as defined in the annotation.
     * <br><br>
     * Currently, supports {@link BooleanListEntry} and {@link SelectionListEntry} dependencies.
     * If a different config entry type is used, an {@link IllegalStateException} will be thrown.
     *
     * @param annotation The {@link ConfigEntry.Gui.DependsOn} annotation defining the dependency
     * @param dependency The depended-on {@link AbstractConfigListEntry}
     * @return The built {@link Dependency}
     * @throws IllegalStateException when an unsupported dependency type is used, or the annotation is somehow invalid
     */
    public static Dependency buildDependency(ConfigEntry.Gui.DependsOn annotation, AbstractConfigListEntry<?> dependency) throws IllegalStateException {
        if (dependency instanceof BooleanListEntry booleanListEntry) {
            return buildDependency(annotation, booleanListEntry);
        } else if (dependency instanceof SelectionListEntry<?> selectionListEntry) {
            return buildDependency(annotation, selectionListEntry);
        } else {
            throw new IllegalStateException("Unsupported dependency type: %s".formatted(dependency.getClass().getSimpleName()));
        }
    }
    
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
    
    public static class GeneratedConfigEntry {
        
        private final String i18n;
        private final Field field;
        private final AbstractConfigListEntry entry;
        
        private GeneratedConfigEntry(String i18n, Field field, AbstractConfigListEntry entry) {
            this.i18n = i18n;
            this.field = field;
            this.entry = entry;
        }
        
        public String getI18n() {
            return i18n;
        }
        
        public Field getField() {
            return field;
        }
        
        public AbstractConfigListEntry getEntry() {
            return entry;
        }
    }
}
