package me.shedaniel.clothconfig2.api.dependencies;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Stream;

public class DependencyGroup implements Dependency {
    
    protected Boolean shouldHide = null;
    private final Condition condition;
    private final Set<Dependency> children = new LinkedHashSet<>();
    
    @ApiStatus.Internal
    @Deprecated
    public DependencyGroup(Condition condition) {
        this.condition = condition;
    }
    
    @Override
    public boolean check() {
        Stream<Dependency> stream = this.children.stream();
        return switch (this.condition) {
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
    public Component getShortDescription() {
        return Component.translatable("text.cloth-config.dependency_groups.short_description", children.size());
    }
    
    public void unsetHiddenWhenNotMet() {
        this.shouldHide = null;
    }
    
    /**
     * {@inheritDoc} For example, <em>Depends on all of "XYZ Toggle" being set to "Yes" and "A cool enum" being one of 3 values.</em>
     */
    @Override
    public Optional<Component[]> getTooltip() {
        Component conditionText = Component.translatable(condition.i18n);
        List<Component> descriptions = children.stream().map(Dependency::getShortDescription).toList();
        
        List<Component> lines = new ArrayList<>();
        switch (children.size()) {
            case 1 -> {
                Dependency child = children.stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Impossible state"));
                if (this.condition == Condition.NONE) {
                    // FIXME this tooltip will be wrong when this.condition is NONE
                }
                return child.getTooltip();
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
         */
        ALL("text.cloth-config.dependency_groups.condition.all"),
    
        /**
         * This condition is true if all dependencies are unmet, i.e. none are met.
         */
        NONE("text.cloth-config.dependency_groups.condition.none"),
    
        /**
         * This condition is true if any dependency is met.
         */
        ANY("text.cloth-config.dependency_groups.condition.any"),
        
        /**
         * This condition is true if exactly one dependency is met.
         */
        ONE("text.cloth-config.dependency_groups.condition.one");
    
        private final String i18n;
    
        Condition(String i18n) {
            this.i18n = i18n;
        }
    }
}
