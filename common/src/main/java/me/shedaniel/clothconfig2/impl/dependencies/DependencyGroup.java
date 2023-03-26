package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;
import java.util.stream.Stream;

public class DependencyGroup implements Dependency {
    
    private final Condition condition;
    private final Set<Dependency> children = new LinkedHashSet<>();
    private final boolean inverted;
    
    private Boolean shouldHide = null;
    
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
    
    @Override
    public boolean hidden() {
        if (check())
            return false;

        // If shouldHide isn't explicitly defined, we should check if any of the groups (unmet) members have shouldHide set
        if (shouldHide == null) {
            // If condition is NONE, it doesn't make sense to use child-dependencies' hidden value
            if (this.condition == Condition.NONE)
                return false;
        
            return this.children.stream()
                    .filter(Dependency::hiddenWhenNotMet)
                    .anyMatch(dependency -> !dependency.check());
        }

        return hiddenWhenNotMet();
    }
    
    public boolean inverted() {
        return this.inverted;
    }
    
    @Override
    public boolean hiddenWhenNotMet() {
        return shouldHide != null && shouldHide;
    }
    
    @Override
    public void hiddenWhenNotMet(boolean shouldHide) {
        this.shouldHide = shouldHide;
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
        // Inversion can be ignored here as we only print how many things are depended on, not what their conditions are
        return Component.translatable("text.cloth-config.dependency_groups.short_description", children.size());
    }
    
    public void unsetHiddenWhenNotMet() {
        this.shouldHide = null;
    }
    
    /**
     * {@inheritDoc} For example, <em>Depends on all of "XYZ Toggle" being enabled and "A cool enum" being one of 3 values.</em>
     */
    public Optional<Component[]> getTooltip(boolean inverted) {
        boolean invert = inverted != inverted();
        Component conditionText = Component.translatable(condition.getI18n(invert));
        List<Component> descriptions = children.stream()
                .map(Dependency::getShortDescription)
                .toList();
        
        List<Component> lines = new ArrayList<>();
        switch (children.size()) {
            case 1 -> {
                // If the group only has one child, return its tooltip instead
                return children.stream()
                        // The condition "none of one" is effectively inversion
                        .map(child -> child.getTooltip(invert != (this.condition == Condition.NONE)))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Can't find any elements in list of 1"));
            }
            case 2 ->
                    lines.add(Component.translatable("text.cloth-config.dependency_groups.two_dependencies", conditionText,
                        MutableComponent.create(descriptions.get(0).getContents()).withStyle(ChatFormatting.ITALIC),
                        MutableComponent.create(descriptions.get(1).getContents()).withStyle(ChatFormatting.ITALIC)));
            default ->
                    lines.add(Component.translatable("text.cloth-config.dependency_groups.many_dependencies", conditionText));
        }
        
        if (children.size() > 2)
            lines.addAll(descriptions.stream()
                .map(description -> Component.translatable("text.cloth-config.dependencies.list_entry", description))
                .toList());
        
        return Optional.of(lines.toArray(Component[]::new));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (obj instanceof DependencyGroup group) {
            if (this.condition != group.condition)
                return false;
            if (this.shouldHide == null) {
                if (group.shouldHide != null)
                    return false;
            } else {
                if (group.shouldHide == null)
                    return false;
                if (this.shouldHide.booleanValue() != group.shouldHide.booleanValue())
                    return false;
            }
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
        return (shouldHide == null ? 0 : shouldHide.hashCode()) + 8*condition.hashCode() + 16*children.hashCode();
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
    }
}
