package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

public abstract class ConfigEntryDependency<T, E extends AbstractConfigEntry<T>> implements Dependency {
    
    private final E entry;
    private final Collection<T> conditions = new ArrayList<>();
    
    private boolean shouldHide = false;
    
    protected ConfigEntryDependency(E entry) {
        this.entry = entry;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <br><br>
     * This implementation checks if any condition matches the depended-on config entry's value.
     */
    @Override
    public boolean check() {
        T value = getEntry().getValue();
        return getConditions().stream().anyMatch(value::equals);
    }
    
    /**
     * Clears any conditions already defined and adds the condition provided
     * <br>
     * You can use {@code addCondition()} to add condition(s) without removing existing conditions.
     * 
     * @param condition the new condition to be set
     */
    public final void setCondition(T condition) {
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
    public final void addCondition(T... conditions) {
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
    public final void addConditions(Collection<T> conditions) {
        this.conditions.addAll(conditions);
    }
    
    /**
     * @return the Config Entry that is depended on
     */
    public final E getEntry() {
        return entry;
    }
    
    /**
     * Get the dependency's conditions.
     * 
     * @return a {@link Collection} containing the dependency's conditions
     */
    public final Collection<T> getConditions() {
        return conditions;
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
     * Gets the localised human-readable text for a dependency's condition
     * 
     * @param condition the condition to get text for
     * @return the localised human-readable text
     */
    protected abstract Component getConditionText(T condition);
    
    /**
     * {@inheritDoc} For example <em>Depends on "Some Config Entry" being set to "YES".</em>
     */
    @Override
    public Optional<Component[]> getTooltip() {
        Collection<T> conditions = getConditions();
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
            conditionTexts.forEach(text -> tooltip.add(Component.translatable("text.cloth-config.dependencies.list_entry", text)));
        }
        
        if (tooltip.isEmpty())
            return Optional.empty();
        
        return Optional.of(tooltip.toArray(new Component[0]));
    }
}
