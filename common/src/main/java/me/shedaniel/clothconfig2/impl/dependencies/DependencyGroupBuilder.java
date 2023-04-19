package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DependencyGroupBuilder extends AbstractDependencyBuilder<Dependency, DependencyGroupBuilder> {
    
    public DependencyGroupBuilder() {}
    
    @Override
    public Dependency build() {
        return finishBuilding(new DependencyGroup());
    }
    
    @Override
    public DependencyGroupBuilder displayTooltips(boolean showTooltips) {
        this.displayTooltips = showTooltips;
        return this;
    }
    
    @Override
    protected Component generateDescription() {
        List<Dependency> dependencies = conditions.stream()
                .filter(Dependency::hasTooltip)
                .toList();
        String amount = String.valueOf(dependencies.size());
        Component requirementText = requirement.inverted(inverted).getText();
    
        return Component.translatable("text.cloth-config.dependency_groups.short_description", requirementText, amount);
    }
    
    /**
     * {@inheritDoc} For example: 
     * <br><em>Depends on all of the following being true:</em>
     * <br><em>- "XYZ Toggle" being enabled</em>
     * <br><em>- "A cool enum" being one of 3 values</em>
     */
    @Override
    protected Function<String, Component[]> generateTooltipProvider() {
        GroupRequirement requirement = this.requirement.inverted(inverted);
    
        // If only one child, return its tooltip
        if (conditions.size() == 1 && !requirement.effectivelyInvertsSingleton()) {
            Dependency child = conditions.iterator().next();
            return child::getTooltip;
        }
    
        // Filter non-tooltip children and flatten same-requirement children
        // TODO flatten children in build() instead of just in tooltips
        List<Dependency> flattened = flattenChildren();
        if (flattened.isEmpty())
            return null;
    
        List<MutableComponent> childLines = flattened.stream()
                .map(Dependency::getShortDescription)
                .map(description -> Component.translatable("text.cloth-config.dependencies.list_entry", description))
                .toList();
    
        GroupRequirement.Simplified simplified = requirement.simplified();
    
        return effectKey -> {
            // First line - "[enabled] when [all] of the following are [true]:"
            Component firstLine = simplified.describe(effectKey);
            return Streams.concat(Stream.of(firstLine), childLines.stream()).toArray(Component[]::new);
        };
    }
    
    @Override
    public DependencyGroupBuilder withRequirement(GroupRequirement requirement) {
        this.requirement = requirement;
        return this;
    }
    
    public DependencyGroupBuilder withChildren(Dependency... dependencies) {
        Collections.addAll(this.conditions, dependencies);
        return this;
    }
    
    public DependencyGroupBuilder withChildren(Collection<Dependency> dependencies) {
        this.conditions.addAll(dependencies);
        return this;
    }
    
    private List<Dependency> flattenChildren() {
        GroupRequirement requirement = this.requirement.inverted(inverted);
        // It doesn't make sense to flatten groups with condition "exactly one"
        if (requirement == GroupRequirement.ONE || requirement == GroupRequirement.NOT_ONE)
            return this.conditions.stream().toList();
        
        List<Dependency> flattened = new LinkedList<>();
        this.conditions.stream()
                .filter(Dependency::hasTooltip)
                .forEach(child -> {
                    if (child instanceof DependencyGroup group) {
                        if (requirement == group.getRequirement()) {
                            flattened.addAll(group.getConditions());
                            return;
                        }
                    }
                    flattened.add(child);
                });
        
        return flattened;
    }
    
}
