package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.StaticCondition;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Represents a dependency on a {@link ConfigEntry}
 *
 * @param <T> the type this dependency deals with
 * @param <E> the {@link ConfigEntry} type
 */
public abstract class ConfigEntryDependency<T, E extends ConfigEntry<T>> extends AbstractDependency<Condition<T>, E> {
    
    protected ConfigEntryDependency(E entry) {
        super(entry);
    }
    
    /**
     * {@inheritDoc}
     *
     * <br><br>
     * This implementation checks if any condition matches the depended-on config entry's value.
     */
    @Override
    public boolean check() {
        T value = getElement().getValue();
        return getConditions().stream().anyMatch(condition -> condition.check(value));
    }
    
    protected Component getConditionText(Condition<T> condition, boolean inverted) {
        return condition instanceof StaticCondition<T> staticCondition ?
                getConditionText(staticCondition, inverted) : condition.getText(inverted);
    }
    
    protected Component getConditionText(StaticCondition<T> condition, boolean inverted) {
        return condition.getText(inverted);
    }
    
    @Override
    public Component getShortDescription(boolean inverted) {
        int conditions = getConditions().size();
        
        if (conditions == 0)
            throw new IllegalStateException("Required at least one condition");
    
        if (conditions == 1) {
            Component conditionText = (this.getConditions().stream()
                    .map(condition -> condition.getText(inverted))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Expected exactly one condition")));
            return Component.translatable("text.cloth-config.dependencies.short_description.single", getElement().getFieldName(), conditionText);
        }
    
        return Component.translatable("text.cloth-config.dependencies.short_description.many", getElement().getFieldName(), conditions);
    }
    
    /**
     * {@inheritDoc} For example <em>Depends on "Some Config Entry" being set to "YES".</em>
     */
    @Override
    public Optional<Component[]> getTooltip(boolean inverted) {
        Collection<Condition<T>> conditions = getConditions();
        if (conditions.isEmpty())
            throw new IllegalStateException("Expected at least one condition to be defined");
        
        // Get the name of the depended-on entry, and style it bold.
        Component dependencyName = MutableComponent.create(getElement().getFieldName().getContents())
                .withStyle(ChatFormatting.BOLD);
        
        // Get the text for each condition, again styled bold.
        List<Component> conditionTexts = conditions.stream()
                .distinct()
                .map(condition -> getConditionText(condition, inverted))
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
}
