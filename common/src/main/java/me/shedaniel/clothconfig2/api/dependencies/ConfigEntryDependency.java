package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

/**
 * Represents a dependency on a {@link AbstractConfigEntry}
 * 
 * @param <T> The type this dependency deals with
 * @param <C> The type used for the condition
 * @param <E> the config entry type
 */
public abstract class ConfigEntryDependency<T, C, E extends AbstractConfigEntry<T>> implements Dependency {
    private final E entry;
    
    private final Collection<C> conditions = new ArrayList<>();
    
    private boolean shouldHide = false;
    
    protected ConfigEntryDependency(E entry) {this.entry = entry;}
    
    /**
     * @return the Config Entry that is depended on
     */
    public final E getEntry() {
        return entry;
    }
    
    @Override
    public final boolean hiddenWhenNotMet() {
        return shouldHide;
    }
    
    @Override
    public final void hiddenWhenNotMet(boolean shouldHide) {
        this.shouldHide = shouldHide;
    }
    
    /**
     * Get the dependency's conditions.
     *
     * @return a {@link Collection} containing the dependency's conditions
     */
    public final Collection<C> getConditions() {
        return conditions;
    }
    
    /**
     * Clears any conditions already defined and adds the condition provided
     * <br>
     * You can use {@code addCondition()} to add condition(s) without removing existing conditions.
     *
     * @param condition the new condition to be set
     */
    public final void setCondition(C condition) {
        conditions.clear();
        conditions.add(condition);
    }
    
    /**
     * Adds one or more conditions to the dependency. If any condition matches the entry's value,
     * then the dependency is met.
     * <br>
     * Unlike {@code setCondition()}, existing conditions are not removed.
     *
     * @param conditions the conditions to be added
     */
    @SafeVarargs //FIXME is this actually safe from heap pollution? T... aka Object[] seems okay-ish?
    public final void addCondition(C... conditions) {
        addConditions(Arrays.asList(conditions));
    }
    
    /**
     * Adds one or more conditions to the dependency. If any condition matches the entry's value,
     * then the dependency is met.
     * <br>
     * Unlike {@code setCondition()}, existing conditions are not removed.
     *
     * @param conditions a {@link Collection} of conditions to be added
     */
    public final void addConditions(Collection<C> conditions) {
        this.conditions.addAll(conditions);
    }
    
    /**
     * Gets the localised human-readable text for a dependency's condition
     *
     * @param condition the condition to get text for
     * @return the localised human-readable text
     */
    protected abstract Component getConditionText(C condition);
    
    /**
     * {@inheritDoc} For example <em>Depends on "Some Config Entry" being set to "YES".</em>
     */
    @Override
    public Optional<Component[]> getTooltip() {
        Collection<C> conditions = getConditions();
        if (conditions.isEmpty())
            throw new IllegalStateException("Expected at least one condition to be defined");
        
        // Get the name of the depended-on entry, and style it bold.
        Component dependencyName = MutableComponent.create(getEntry().getFieldName().getContents())
                .withStyle(ChatFormatting.BOLD);
        
        // Get the text for each condition, again styled bold.
        List<MutableComponent> conditionTexts = conditions.stream()
                .distinct()
                .map(this::getConditionText)
                .map(text -> MutableComponent.create(text.getContents()))
                .map(text -> text.withStyle(ChatFormatting.BOLD))
                .toList();
        
        // Generate a slightly different tooltip depending on how many conditions are defined
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(switch (conditionTexts.size()) {
            case 1 -> Component.translatable("text.cloth-config.dependencies.one_condition", dependencyName, conditionTexts.get(0));
            case 2 -> Component.translatable("text.cloth-config.dependencies.two_conditions", dependencyName, conditionTexts.get(0), conditionTexts.get(1));
            default -> Component.translatable("text.cloth-config.dependencies.many_conditions", dependencyName);
        });
        
        // If many conditions, print them as a list
        if (conditionTexts.size() > 2) {
            tooltip.addAll(conditionTexts.stream()
                    .map(text -> Component.translatable("text.cloth-config.dependencies.list_entry", text))
                    .toList());
        }
        
        if (tooltip.isEmpty())
            return Optional.empty();
        
        return Optional.of(tooltip.toArray(Component[]::new));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (obj instanceof ConfigEntryDependency<?,?,?> dependency) {
            if (this.shouldHide != dependency.shouldHide)
                return false;
            if (!this.entry.equals(dependency.entry))
                return false;
            if (this.conditions.size() != dependency.conditions.size())
                return false;
            // True if all conditions have an equivalent
            return this.conditions.stream().allMatch(condition ->
                    dependency.conditions.stream().anyMatch(condition::equals));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Boolean.hashCode(this.shouldHide) + 8*this.entry.hashCode() + 16*this.conditions.hashCode();
    }
}
