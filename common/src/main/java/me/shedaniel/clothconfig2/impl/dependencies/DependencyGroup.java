package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DependencyGroup implements Dependency {
    
    private static final Predicate<Dependency> ALL = dependency -> true;
    private static final Predicate<Dependency> MET = Dependency::check;
    private static final Predicate<Dependency> UNMET = dependency -> !dependency.check();
    
    private final Condition condition;
    private final Set<Dependency> children = new LinkedHashSet<>();
    private final boolean inverted;
    
    DependencyGroup(Condition condition, boolean inverted) {
        this.condition = condition;
        this.inverted = inverted;
    }
    
    @Override
    public boolean check() {
        Stream<Dependency> stream = this.children.stream();
        return inverted() != switch (this.condition) {
            case ALL -> stream.allMatch(Dependency::check);
            case NONE -> stream.noneMatch(Dependency::check);
            case ANY -> stream.anyMatch(Dependency::check);
            case ONE -> stream.filter(Dependency::check).count() == 1;
        };
    }
    
    public boolean inverted() {
        return this.inverted;
    }
    
    /**
     * Adds one or more children to the dependency.
     *
     * @param children a {@link Collection} of child dependencies to be added
     */
    public final void addChildren(Collection<Dependency> children) {
        this.children.addAll(children);
    }
    
    /**
     * Adds one or more children to the dependency.
     *
     * @param children one or more child dependencies to be added
     */
    public final void addChildren(Dependency... children) {
        Collections.addAll(this.children, children);
    }
    
    @Override
    public Component getShortDescription(boolean inverted) {
        boolean invert = inverted != inverted();
        List<Dependency> dependencies = flatChildren(invert, ALL);
        int amount = dependencies.size();
        switch (amount) {
            case 1 -> {
                return dependencies.stream()
                        .map(child -> getShortDescription(shouldInvertChild(invert)))
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("Can't find any elements in list of 1"));
            }
            case 2 -> {
                return Component.translatable("text.cloth-config.dependency_groups.short_description.two",
                        Component.translatable(condition.getI18n(invert)),
                        dependencies.get(0).getShortDescription(),
                        Component.translatable(condition.getJoiningI18n(invert)),
                        dependencies.get(1).getShortDescription());
            }
            default -> {
                return Component.translatable("text.cloth-config.dependency_groups.short_description.many",
                        Component.translatable(condition.getI18n(invert)),
                        amount);
            }
        }
    }
    
    /**
     * {@inheritDoc} For example: 
     * <br><em>Depends on all of the following being true:</em>
     * <br><em>- "XYZ Toggle" being enabled</em>
     * <br><em>- "A cool enum" being one of 3 values</em>
     */
    public Optional<Component[]> getTooltip(boolean inverted) {
        // Only return a tooltip if the group condition is unmet
        if (inverted != check())
            return Optional.empty();

        boolean invert = inverted != inverted();
        
        // If only one child, return its tooltip
        if (children.size() == 1) {
            return children.stream()
                    .map(child -> child.getTooltip(shouldInvertChild(invert)))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Can't find any elements in list of 1"));
        }
        
        // Handle inverted conditions
        if (invert) {
            return Optional.of(switch (condition) {
                case ALL -> // "Not all" - at least one child must be false
                        tooltipFor(Condition.ANY, flatChildren(true, MET), false);
                case NONE -> // Effectively "any" - at least one child must be true
                        tooltipFor(Condition.ANY, flatChildren(true, UNMET), true);
                case ANY -> // Effectively "none" - all children must be false
                        tooltipFor(Condition.ALL, flatChildren(true, MET), false);
                case ONE -> // "not one" - none or multiple children must be true
                        tooltipFor(Condition.ONE.getI18n(true), flatChildren(true, ALL), true);
            });
        }
    
        // Handle normal conditions
        return Optional.of(switch (condition) {
            case ALL -> // All children must be true
                    tooltipFor(Condition.ALL, flatChildren(UNMET), true);
            case NONE -> // "None" - all children must be false
                    tooltipFor(Condition.ALL, flatChildren(MET), false);
            case ANY -> // "Any" - at least one child must be true
                    tooltipFor(Condition.ANY, flatChildren(UNMET), true);
            case ONE -> // Exactly one child must be true:
                    tooltipFor(Condition.ONE, flatChildren(ALL), true);
        });
    }
    
    private List<Dependency> flatChildren(Predicate<Dependency> predicate) {
        return flatChildren(false, predicate);
    }
    
    private List<Dependency> flatChildren(boolean inverted, Predicate<Dependency> predicate) {
        // It doesn't make sense to flatten groups with condition "exactly one"
        if (condition == Condition.ONE)
            return children.stream()
                    .filter(predicate)
                    .toList();
        
        List<Dependency> flattened = new LinkedList<>();
        children.stream()
                .filter(predicate)
                .forEach(child -> {
            if (child instanceof DependencyGroup group) {
                // TODO can we flatten in some inverted scenarios?
                if (condition == group.condition && !(inverted || group.inverted())) {
                    flattened.addAll(group.flatChildren(inverted, predicate));
                    return;
                }
            }
            flattened.add(child);
        });
        
        return flattened;
    }
    
    private boolean shouldInvertChild(boolean inverted) {
        // Check if the condition is effectively inversion when dealing with only one child
        return switch (condition) {
            case ALL, ANY, ONE -> inverted; // met if only child is true
            case NONE -> !inverted;         // met if only child is false
        };
    }
    
    private List<Dependency> met() {
        return children.stream()
                .filter(Dependency::check)
                .toList();
    }
    
    private List<Dependency> unmet() {
        return children.stream()
                .filter(dependency -> !dependency.check())
                .toList();
    }
    
    private Component[] tooltipFor(Condition condition, Collection<Dependency> dependencies, boolean value) {
        if (dependencies.size() == 1) {
            // TODO handle cases with only one or two met
        }
        return tooltipFor(condition.i18n, dependencies, value);
    }
    
    private Component[] tooltipFor(String conditionKey, Collection<Dependency> dependencies, boolean value) {
        Component conditionText = Component.translatable(conditionKey)
                .withStyle(ChatFormatting.BOLD);
        Component valueText = Component.translatable(value ? "text.cloth-config.true" : "text.cloth-config.false")
                .withStyle(ChatFormatting.BOLD);
        
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("text.cloth-config.dependency_groups.tooltip", conditionText, valueText));
        lines.addAll(dependencies.stream()
                        .map(Dependency::getShortDescription)
                        .map(description -> Component.translatable("text.cloth-config.dependencies.list_entry", description))
                        .toList());

        return lines.toArray(Component[]::new);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (obj instanceof DependencyGroup group) {
            if (this.condition != group.condition)
                return false;
            if (this.children.size() != group.children.size())
                return false;
            // True if every child has an equivalent
            return children.stream().allMatch(child ->
                    group.children.stream().anyMatch(child::equals));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 8 * condition.hashCode() + 16 * children.hashCode();
    }
    
    /**
     * Defines a condition for a {@link DependencyGroup} to be met.
     */
    public enum Condition {

        /**
         * This condition is true if all dependencies are met, i.e. none are unmet.
         * 
         * <p>Effectively logical AND, the inverse of {@code NAND}.
         */
        ALL("text.cloth-config.dependency_groups.condition.all"),
    
        /**
         * This condition is true if all dependencies are unmet, i.e. none are met.
         * 
         * <p>Effectively logical NOR, the inverse of {@link Condition#ANY OR}.
         */
        NONE("text.cloth-config.dependency_groups.condition.none"),
    
        /**
         * This condition is true if any dependency is met.
         * 
         * <p>Effectively logical OR, the inverse of {@link Condition#NONE NOR}.
         */
        ANY("text.cloth-config.dependency_groups.condition.any"),
        
        /**
         * This condition is true if exactly one dependency is met.
         * 
         * <p>Effectively logical XOR, the inverse of {@code XNOR}.
         */
        ONE("text.cloth-config.dependency_groups.condition.one");
    
        private final String i18n;
    
        Condition(String i18n) {
            this.i18n = i18n;
        }
    
        public String getI18n(boolean inverted) {
            if (!inverted)
                return this.i18n;
            
            return switch (this) {
                case ALL -> "text.cloth-config.dependency_groups.condition.not_all";
                case ANY -> NONE.i18n;
                case NONE -> ANY.i18n;
                case ONE -> "text.cloth-config.dependency_groups.condition.not_one";
            };
        }
    
        public String getJoiningI18n(boolean invert) {
            final String and = "text.cloth-config.and";
            final String or = "text.cloth-config.or";
            return switch (this) {
                case ALL -> invert ? or : and;
                case NONE -> invert ? and : or;
                case ANY -> or;
                case ONE -> and;
            };
        }
    }
}
