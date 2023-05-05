package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Dependency.EnableIf;
import me.shedaniel.autoconfig.util.RelativeI18n;
import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ComparisonOperator;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.MatcherConditionBuilder;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.PredicateConditionBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

record MatcherConditionDefinition(EnumSet<ConditionFlag> flags, @Nullable ComparisonOperator operator, String i18n) {
    
    /**
     * @see StaticConditionDefinition
     * @see EnableIf#matching() Public API documentation
     */
    static MatcherConditionDefinition fromConditionString(String i18nBase, String condition) {
        // Parse any flags
        StaticConditionDefinition definition = StaticConditionDefinition.fromConditionString(condition)
                .map(String::strip);
    
        // Parse any operators
        ComparisonOperator operator = ComparisonOperator.startsWith(definition.condition());
        if (operator != null)
            definition = definition
                    .map(string -> string.substring(operator.toString().length()))
                    .map(String::stripLeading);
    
        // Parse the i18n reference
        definition = definition.map(string -> RelativeI18n.parse(i18nBase, string));
        
        return new MatcherConditionDefinition(definition.flags(), operator, definition.condition());
    }
    
    private boolean inverted() {
        return this.flags().contains(ConditionFlag.INVERTED);
    }
    
    private boolean ignoreCase() {
        return this.flags().contains(ConditionFlag.IGNORE_CASE);
    }
    
    Condition<String> toStringMatcher(ConfigEntry<String> gui) {
        Predicate<String> matches = this.ignoreCase() ?
                value -> gui.getValue().equalsIgnoreCase(value)
                : value -> gui.getValue().equals(value);
        
        Component description = Component.translatable("text.cloth-config.dependencies.matches", gui.getFieldName());
        if (this.inverted())
            description = Component.translatable("text.cloth-config.dependencies.conditions.not", description);
        
        return new PredicateConditionBuilder<>(matches)
                .setInverted(this.inverted())
                .setDescription(description)
                .build();
    }
    
    <T> Condition<T> toMatcher(ConfigEntry<T> gui) {
        return new MatcherConditionBuilder<>(gui)
                .setInverted(this.inverted())
                .build();
    }
    
    <T extends Comparable<T>> Condition<T> toComparativeMatcher(ConfigEntry<T> gui) {
        ComparisonOperator operator = Optional.ofNullable(this.operator())
                .orElse(ComparisonOperator.EQUAL)
                .inverted(this.inverted());
        Predicate<T> predicate = value -> operator.compare(value, gui.getValue());
        
        return new PredicateConditionBuilder<>(predicate)
                .setDescription(operator.description(gui))
                .build();
    }
}
