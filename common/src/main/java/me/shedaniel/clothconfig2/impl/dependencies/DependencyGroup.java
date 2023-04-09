package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.stream.Stream;

public class DependencyGroup implements Dependency {
    
    private GroupRequirement requirement = GroupRequirement.ANY;
    private final Set<Dependency> children = new LinkedHashSet<>();
    private final boolean generateTooltip;
    
    DependencyGroup(boolean generateTooltip) {
        this.generateTooltip = generateTooltip;
    }
    
    @Override
    public boolean check() {
        return this.requirement.matches(this.children, Dependency::check);
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
        List<Dependency> dependencies = processChildrenForTooltip();
        int amount = dependencies.size();
        
        if (amount == 1)
            return dependencies.iterator().next()
                    .getShortDescription(shouldInvertOnlyChild(inverted));
        
        return Component.translatable("text.cloth-config.dependency_groups.short_description.many",
                    Component.translatable(requirement.inverted(inverted).getI18n()), amount);
    }
    
    /**
     * {@inheritDoc} For example: 
     * <br><em>Depends on all of the following being true:</em>
     * <br><em>- "XYZ Toggle" being enabled</em>
     * <br><em>- "A cool enum" being one of 3 values</em>
     */
    public Optional<Component[]> getTooltip(boolean inverted, String effectKey) {
        if (!generateTooltip)
            return Optional.empty();
    
        // If only one child, return its tooltip
        if (children.size() == 1)
            return children.iterator().next().getTooltip(shouldInvertOnlyChild(inverted), effectKey);
        
        // Filter non-tooltip children and flatten same-requirement children
        List<Dependency> flattened = processChildrenForTooltip();
        if (flattened.isEmpty())
            return Optional.empty();
    
        GroupRequirement.Simplified simple = this.requirement.inverted(inverted).simplified();
    
        Component[] tooltip = Streams.concat(
                // First line - "[enabled] when [all] of the following are [true]:"
                Stream.of(simple.describe(effectKey)),
                // Additional lines
                flattened.stream()
                        .map(Dependency::getShortDescription)
                        .map(description -> Component.translatable("text.cloth-config.dependencies.list_entry", description))
        ).toArray(Component[]::new);
        
        return Optional.of(tooltip);
    }
    
    @Override
    public boolean hasTooltip() {
        return generateTooltip;
    }
    
    @Override
    public void setRequirement(GroupRequirement requirement) {
        this.requirement = requirement;
    }
    
    @Override
    public GroupRequirement getRequirement() {
        return this.requirement;
    }
    
    private List<Dependency> processChildrenForTooltip() {
        // It doesn't make sense to flatten groups with condition "exactly one"
        if (requirement == GroupRequirement.ONE)
            return children.stream().toList();
        
        List<Dependency> flattened = new LinkedList<>();
        children.stream()
                .filter(Dependency::hasTooltip)
                .forEach(child -> {
            if (child instanceof DependencyGroup group) {
                if (requirement == group.requirement) {
                    flattened.addAll(group.processChildrenForTooltip());
                    return;
                }
            }
            flattened.add(child);
        });
        
        return flattened;
    }
    
    private boolean shouldInvertOnlyChild(boolean inverted) {
        // Check if the condition is effectively inversion when dealing with only one child
        return switch (requirement) {
            case ALL, ANY, ONE -> inverted;           // met if only child is true
            case NONE, NOT_ALL, NOT_ONE -> !inverted; // met if only child is false
        };
    }
}
