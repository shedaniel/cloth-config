package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
        if (condition instanceof EqualityCondition<T> equalityCondition)
            return Component.translatable("text.cloth-config.dependencies.is", getStaticConditionText(equalityCondition, inverted));
    
        Component text = MutableComponent.create(condition.getText(inverted).getContents()).withStyle(ChatFormatting.BOLD);
        return Component.translatable("text.cloth-config.dependencies.matches", text);
    }
    
    protected Component getStaticConditionText(EqualityCondition<T> condition, boolean inverted) {
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
    public Optional<Component[]> getTooltip(boolean inverted, String effectKey) {
        if (!hasTooltip())
            return Optional.empty();
        
        Collection<Condition<T>> conditions = getConditions();
        if (conditions.isEmpty())
            throw new IllegalStateException("Expected at least one condition to be defined");
        
        // Get the text for each condition, again styled bold.
        List<Component> conditionTexts = conditions.stream()
                .distinct()
                .map(condition -> getConditionText(condition, inverted))
                .toList();
        
        // Build the main line of the tooltip
        Component effect = Component.translatable(effectKey);
        Component gui = MutableComponent.create(getElement().getFieldName().getContents())
                .withStyle(ChatFormatting.BOLD);
        Component condition = switch (conditionTexts.size()) {
            case 1 -> Component.translatable("text.cloth-config.dependencies.one_condition", conditionTexts.get(0));
            case 2 -> Component.translatable("text.cloth-config.dependencies.two_conditions", conditionTexts.get(0), conditionTexts.get(1));
            default -> Component.translatable("text.cloth-config.dependencies.many_conditions");
        };
    
        // Build the final array using a stream of lines
        Stream<Component> stream = Stream.of(Component.translatable("text.cloth-config.dependencies.tooltip", effect, gui, condition));
    
        // If many conditions, print them as a list
        if (conditionTexts.size() > 2) {
            stream = Streams.concat(stream, conditionTexts.stream()
                    .map(text -> Component.translatable("text.cloth-config.dependencies.list_entry", text)));
        }
    
        Component[] tooltip = stream.toArray(Component[]::new);
    
        return tooltip.length > 0 ? Optional.of(tooltip) : Optional.empty();
    }
}
