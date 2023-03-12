package me.shedaniel.clothconfig2.api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

public abstract class Dependency<T, E extends AbstractConfigEntry<T>> {
    
    private final E entry;
    private final Collection<T> conditions = new ArrayList<>();
    
    private boolean shouldHide = false;
    
    protected Dependency(E entry) {
        this.entry = entry;
    }
    
    public boolean check() {
        T value = getEntry().getValue();
        
        // Dependency is satisfied if value matches any condition
        return getConditions().stream().anyMatch(value::equals);
    }
    
    /**
     * Clears any conditions already defined and adds the condition provided
     * 
     * @param condition the new condition to be set
     */
    public final void setCondition(T condition) {
        conditions.clear();
        conditions.add(condition);
    }
    
    public final void addCondition(T... conditions) {
        this.conditions.addAll(Arrays.asList(conditions));
    }
    
    public final E getEntry() {
        return entry;
    }
    
    public final Collection<T> getConditions() {
        return conditions.stream().toList();
    }
    
    public final boolean isHiddenWhenDisabled() {
        return shouldHide;
    }
    
    public final void setHiddenWhenDisabled(boolean shouldHide) {
        this.shouldHide = shouldHide;
    }
    
    /**
     * Gets the localised human-readable text for a dependency's condition
     * 
     * @param condition the condition to get text for
     * @return the localised human-readable text
     */
    protected abstract Component getConditionText(T condition);
    
    public Optional<Component[]> getTooltip() {
        // TODO consider showing the tooltip whenever a dependency exists?
        if (check())
            return Optional.empty();
        
        Collection<T> conditions = getConditions();
        if (conditions.isEmpty())
            throw new IllegalStateException("Expected at least one condition to be set");
        
        Component dependencyName = MutableComponent.create(getEntry().getFieldName().getContents())
                .withStyle(ChatFormatting.BOLD);
        
        List<MutableComponent> conditionNames = conditions.stream()
                .distinct()
                .map(this::getConditionText)
                .map(component -> MutableComponent.create(component.getContents()))
                .map(component -> component.withStyle(ChatFormatting.BOLD))
                .toList();

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(switch (conditionNames.size()) {
            case 1 -> Component.translatable("text.cloth-config.dependencies.one_condition", dependencyName, conditionNames.get(0));
            case 2 -> Component.translatable("text.cloth-config.dependencies.two_condition", dependencyName, conditionNames.get(0), conditionNames.get(1));
            default -> Component.translatable("text.cloth-config.dependencies.many_conditions", dependencyName);
        });
        
        // If many, give each value its own line
        if (conditionNames.size() > 2) {
            conditionNames.forEach(conditionName -> tooltip.add(Component.translatable("text.cloth-config.dependencies.list_entry", conditionName)));
        }
        
        return Optional.of(tooltip.toArray(new Component[0]));
    }
}
