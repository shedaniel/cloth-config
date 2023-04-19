package me.shedaniel.clothconfig2.api.dependencies.requirements;

import me.shedaniel.clothconfig2.impl.dependencies.DependencyGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Defines a condition for a {@link DependencyGroup} to be met.
 */
public enum GroupRequirement implements Requirement<GroupRequirement> {
    
    /**
     * This condition is true if all dependencies are met, i.e. none are unmet.
     *
     * <p>Effectively logical AND, the inverse of {@link #NOT_ALL NAND}.
     */
    ALL("text.cloth-config.group_requirement.all"),
    
    /**
     * This condition is true if not all dependencies are met, i.e. any are unmet.
     *
     * <p>Effectively logical NAND, the inverse of {@link #ALL AND}.
     */
    NOT_ALL("text.cloth-config.group_requirement.not_all"),
    
    /**
     * This condition is true if all dependencies are unmet, i.e. none are met.
     *
     * <p>Effectively logical NOR, the inverse of {@link #ANY OR}.
     */
    NONE("text.cloth-config.group_requirement.none"),
    
    /**
     * This condition is true if any dependency is met.
     *
     * <p>Effectively logical OR, the inverse of {@link #NONE NOR}.
     */
    ANY("text.cloth-config.group_requirement.any"),
    
    /**
     * This condition is true if exactly one dependency is met.
     *
     * <p>Effectively logical XOR, the inverse of {@link #NOT_ONE XNOR}.
     */
    ONE("text.cloth-config.group_requirement.one"),
    
    /**
     * This condition is true if either zero or multiple dependencies are met.
     *
     * <p>Effectively logical XNOR, the inverse of {@link #ONE XNOR}.
     */
    NOT_ONE("text.cloth-config.group_requirement.not_one");
    
    private final String i18n;
    
    GroupRequirement(String i18n) {
        this.i18n = i18n;
    }
    
    public String getI18n() {
        return this.i18n;
    }
    
    public String getJoiningI18n() {
        return switch (this) {
            case ALL, NOT_ALL, ONE, NOT_ONE -> "text.cloth-config.and";
            case NONE, ANY -> "text.cloth-config.or";
        };
    }
    
    public Component getText() {
        return Component.translatable(getI18n());
    }
    
    public Component getJoiningText() {
        return Component.translatable(getJoiningI18n());
    }
    
    public <T> boolean matches(Collection<T> collection, Predicate<? super T> predicate) {
        Stream<T> stream = collection.stream();
        return switch (this) {
            case ALL -> stream.allMatch(predicate);
            case NOT_ALL -> stream.anyMatch(predicate.negate());
            case NONE -> stream.noneMatch(predicate);
            case ANY -> stream.anyMatch(predicate);
            case ONE -> stream.filter(predicate).count() == 1;
            case NOT_ONE -> stream.filter(predicate).count() != 1;
        };
    }
    
    public boolean effectivelyInvertsSingleton() {
        return switch (this) {
            case ALL, ANY, ONE -> false;           // met if only child is true
            case NONE, NOT_ALL, NOT_ONE -> true; // met if only child is false
        };
    }
    
    @Override
    public GroupRequirement inverted(boolean inverted) {
        if (!inverted)
            return this;
        
        return switch (this) {
            case ALL -> NOT_ALL;
            case ANY -> NONE;
            case NOT_ALL -> ALL;
            case NONE -> ANY;
            case ONE -> NOT_ONE;
            case NOT_ONE -> ONE;
        };
    }
    
    public Simplified simplified() {
        return switch (this) {
            case ALL -> new Simplified(ALL, true);
            case NOT_ALL -> new Simplified(ANY, false);
            case NONE -> new Simplified(ALL, false);
            case ANY -> new Simplified(ANY, true);
            case ONE -> new Simplified(ONE, true);
            case NOT_ONE -> new Simplified(NOT_ONE, true);
        };
    }
    
    public record Simplified(GroupRequirement requirement, boolean condition) {
        
        public Component describe(String prefixKey) {
            Component prefix = Component.translatable(prefixKey);
            Component conditionText = Component.translatable(requirement().getI18n())
                    .withStyle(ChatFormatting.BOLD);
            Component valueText = Component.translatable(condition() ? "text.cloth-config.true" : "text.cloth-config.false")
                    .withStyle(ChatFormatting.BOLD);
            
            // "[enabled] when [all] of the following are [true]"
            return Component.translatable("text.cloth-config.dependency_groups.tooltip", prefix, conditionText, valueText);
        }
    }
}
