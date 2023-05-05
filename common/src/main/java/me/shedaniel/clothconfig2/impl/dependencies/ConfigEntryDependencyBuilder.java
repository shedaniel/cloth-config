package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.MatcherConditionBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @param <T> the type the dependency deals with
 * @param <SELF> the type to be returned by chainable methods
 */
public abstract class ConfigEntryDependencyBuilder<T, SELF extends ConfigEntryDependencyBuilder<T, SELF>> extends AbstractDependencyBuilder<Condition<T>, SELF> {
    
    protected final ConfigEntry<T> gui;
    
    protected ConfigEntryDependencyBuilder(ConfigEntry<T> gui) {
        this.gui = gui;
    }
    
    @Override
    public Dependency build() {
        return finishBuilding(new ConfigEntryDependency<>(this.gui));
    }
    
    /**
     * Generates a simple {@link Condition condition} that compares the given {@code value} against the depended-on
     * config entry's value. The condition is considered met if the two values are equal.
     * <br><br>
     * The generated condition will be added to the dependency being built.
     * 
     * @param value a condition value to be checked against the depended-on config entry 
     * @return this instance, for chaining
     */
    public abstract SELF matching(T value);
    
    /**
     * Generates a simple {@link Condition matcher condition} that compares the given {@code gui}'s value against the depended-on
     * config entry's value.
     * <br><br>
     * The generated condition will be added to the dependency being built.
     *
     * @param gui the gui whose value should be compared with the depended-on gui's
     * @return this instance, for chaining
     */
    public SELF matching(ConfigEntry<T> gui) {
        return matching(new MatcherConditionBuilder<>(gui).build());
    }
    
    public SELF matching(Condition<T> condition) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.conditions.add(condition);
        
        return self;
    }
    
    /**
     * Add multiple {@link Condition conditions} to the dependency being built.
     *
     * @param conditions a {@link Collection} containing {@link Condition conditions} to be added to the dependency being built
     * @return this instance, for chaining
     */
    public SELF matching(Collection<? extends Condition<T>> conditions) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        
        this.conditions.addAll(conditions);
        
        return self;
    }
    
    @Override
    protected Component generateDescription() {
        int conditions = this.conditions.size();
    
        if (conditions == 0)
            throw new IllegalStateException("Required at least one condition");
    
        if (conditions == 1) {
            Component conditionText = (this.conditions.stream()
                    .map(condition -> condition.fullDescription(this.requirement.effectivelyInvertsSingleton()))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Expected exactly one condition")));
            return Component.translatable("text.cloth-config.dependencies.short_description.single",
                    this.gui.getFieldName(), conditionText);
        }
    
        return Component.translatable("text.cloth-config.dependencies.short_description.many", this.gui.getFieldName(), this.requirement.getText(), String.valueOf(conditions));
    }
    
    /**
     * {@inheritDoc} For example <em>Depends on "Some Config Entry" being set to "YES".</em>
     */
    @Override
    protected Function<String, Component[]> generateTooltipProvider() {
        if (conditions.isEmpty())
            throw new IllegalStateException("Expected at least one condition to be defined");
    
        // Get the text for each condition, styled bold.
        List<Condition<T>> conditions = this.conditions.stream()
                .distinct()
                .toList();
    
        // Build the main line of the tooltip
        Component gui = MutableComponent.create(this.gui.getFieldName().getContents())
                .withStyle(ChatFormatting.BOLD);
        Component condition = switch (conditions.size()) {
            case 1 -> Component.translatable("text.cloth-config.dependencies.one_condition", conditions.get(0).fullDescription(requirement.effectivelyInvertsSingleton()));
            case 2 -> Component.translatable("text.cloth-config.dependencies.two_conditions",
                    conditions.get(0).fullDescription(false), requirement.getJoiningText(), conditions.get(1).fullDescription(false));
            default -> Component.translatable("text.cloth-config.dependencies.many_conditions", requirement.getText());
        };
    
        if (conditions.size() > 2)  {
            List<MutableComponent> conditionLines = conditions.stream()
                    .map(Condition::description)
                    .map(description -> Component.translatable("text.cloth-config.dependencies.list_entry", description))
                    .toList();
            
            return effectKey -> {
                Component firstLine = Component.translatable("text.cloth-config.dependencies.tooltip", Component.translatable(effectKey), gui, condition);
                return Streams.concat(Stream.of(firstLine), conditionLines.stream()).toArray(Component[]::new);
            };
        }
        
        return effectKey -> {
            MutableComponent tooltip = Component.translatable("text.cloth-config.dependencies.tooltip", Component.translatable(effectKey), gui, condition);
            return Stream.of(tooltip).toArray(Component[]::new);
        };
    }
}
