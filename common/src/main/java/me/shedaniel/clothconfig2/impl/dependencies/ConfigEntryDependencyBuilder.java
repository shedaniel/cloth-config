package me.shedaniel.clothconfig2.impl.dependencies;

import com.google.common.collect.Streams;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.GenericMatcherCondition;
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
 * @param <E> the {@link ConfigEntry} type depended-on
 * @param <D> the {@link Dependency} type that will be built
 * @param <SELF> the type to be returned by chainable methods
 */
public abstract class ConfigEntryDependencyBuilder<T, E extends ConfigEntry<T>, D extends ConfigEntryDependency<T, E>, SELF extends ConfigEntryDependencyBuilder<T, E, D, SELF>> extends AbstractDependencyBuilder<Condition<T>, D, SELF> {
    
    protected final E gui;
    
    protected ConfigEntryDependencyBuilder(E gui) {
        this.gui = gui;
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
     * Generates a simple {@link GenericMatcherCondition} that compares the given {@code gui}'s value against the depended-on
     * config entry's value.
     * <br><br>
     * The generated condition will be added to the dependency being built.
     *
     * @param gui the gui whose value should be compared with the depended-on gui's
     * @return this instance, for chaining
     */
    public SELF matching(ConfigEntry<T> gui) {
        return matching(new GenericMatcherCondition<>(gui));
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
    public SELF withRequirement(GroupRequirement requirement) {
        @SuppressWarnings("unchecked") SELF self = (SELF) this;
        this.requirement = requirement;
        return self;
    }
    
    @Override
    protected Component generateDescription() {
        int conditions = this.conditions.size();
    
        if (conditions == 0)
            throw new IllegalStateException("Required at least one condition");
    
        if (conditions == 1) {
            Component conditionText = (this.conditions.stream()
                    .map(condition -> condition.getText(inverted))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Expected exactly one condition")));
            return Component.translatable("text.cloth-config.dependencies.short_description.single", this.gui.getFieldName(), conditionText);
        }
    
        return Component.translatable("text.cloth-config.dependencies.short_description.many", this.gui.getFieldName(), conditions);
    }
    
    /**
     * {@inheritDoc} For example <em>Depends on "Some Config Entry" being set to "YES".</em>
     */
    @Override
    protected Function<String, Component[]> generateTooltipProvider() {
        if (conditions.isEmpty())
            throw new IllegalStateException("Expected at least one condition to be defined");
    
        // Get the text for each condition, styled bold.
        List<Component> conditionTexts = conditions.stream()
                .distinct()
                .map(condition -> condition.getText(inverted))// TODO use NewCondition.getDescription()
                .toList();
    
        // Build the main line of the tooltip
        Component gui = MutableComponent.create(this.gui.getFieldName().getContents())
                .withStyle(ChatFormatting.BOLD);
        Component condition = switch (conditionTexts.size()) {
            // FIXME support condition GroupRequirement
            case 1 -> Component.translatable("text.cloth-config.dependencies.one_condition", conditionTexts.get(0));
            case 2 -> Component.translatable("text.cloth-config.dependencies.two_conditions", conditionTexts.get(0), conditionTexts.get(1));
            default -> Component.translatable("text.cloth-config.dependencies.many_conditions");
        };
    
        if (conditionTexts.size() > 2)
            return effectKey -> 
                    Streams.concat(
                                Stream.of(Component.translatable("text.cloth-config.dependencies.tooltip", Component.translatable(effectKey), gui, condition)),
                                conditionTexts.stream().map(text -> Component.translatable("text.cloth-config.dependencies.list_entry", text)))
                        .toArray(Component[]::new);
        
        return effectKey -> 
                Stream.of(Component.translatable("text.cloth-config.dependencies.tooltip", Component.translatable(effectKey), gui, condition))
                        .toArray(Component[]::new);
    }
}
