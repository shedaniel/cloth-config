package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DependencyGroup extends AbstractDependency<Dependency> {
    
    DependencyGroup() {}
    
    @Override
    public boolean check() {
        return this.getRequirement().matches(this.getConditions(), Dependency::check);
    }
    
    @Override
    public Component getShortDescription(boolean inverted) {
        List<Dependency> dependencies = processChildrenForTooltip();
        int amount = dependencies.size();
        
        if (amount == 1)
            return dependencies.iterator().next()
                    .getShortDescription(shouldInvertOnlyChild(inverted));
        
        return Component.translatable("text.cloth-config.dependency_groups.short_description.many",
                    Component.translatable(getRequirement().inverted(inverted).getI18n()), amount);
    }
    
    /**
     * {@inheritDoc} For example: 
     * <br><em>Depends on all of the following being true:</em>
     * <br><em>- "XYZ Toggle" being enabled</em>
     * <br><em>- "A cool enum" being one of 3 values</em>
     */
    public Optional<Component[]> getTooltip(boolean inverted, String effectKey) {
        if (!this.hasTooltip())
            return Optional.empty();
    
        // If only one child, return its tooltip
        if (getConditions().size() == 1)
            return getConditions().iterator().next().getTooltip(shouldInvertOnlyChild(inverted), effectKey);
        
        // Filter non-tooltip children and flatten same-requirement children
        List<Dependency> flattened = processChildrenForTooltip();
        if (flattened.isEmpty())
            return Optional.empty();
    
        GroupRequirement.Simplified simple = this.getRequirement().inverted(inverted).simplified();
    
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
    
    private List<Dependency> processChildrenForTooltip() {
        // It doesn't make sense to flatten groups with condition "exactly one"
        if (getRequirement() == GroupRequirement.ONE)
            return getConditions().stream().toList();
        
        List<Dependency> flattened = new LinkedList<>();
        getConditions().stream()
                .filter(Dependency::hasTooltip)
                .forEach(child -> {
            if (child instanceof DependencyGroup group) {
                if (getRequirement() == group.getRequirement()) {
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
        return switch (getRequirement()) {
            case ALL, ANY, ONE -> inverted;           // met if only child is true
            case NONE, NOT_ALL, NOT_ONE -> !inverted; // met if only child is false
        };
    }
}
