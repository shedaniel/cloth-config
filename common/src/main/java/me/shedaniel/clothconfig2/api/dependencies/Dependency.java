package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ComparisonOperator;
import me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.*;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.ComparativeStaticCondition;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.EnumStaticCondition;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static me.shedaniel.clothconfig2.api.dependencies.requirements.GroupRequirement.*;

public interface Dependency {
    
    /**
     * Returns an object that can be used to build various dependencies.
     */
    static @NotNull StartDependencyBuilder builder() {
        //noinspection deprecation
        return InitialDependencyBuilder.getBuilder();
    }
    
    /**
     * Generates a {@link BooleanDependency} that checks if the {@code gui}'s value is {@code true}.
     */
    static @NotNull Dependency isTrue(BooleanListEntry gui) {
        return builder()
                .dependingOn(gui)
                .matching(true)
                .build();
    }
    
    /**
     * Generates a {@link BooleanDependency} that checks if the {@code gui}'s value is {@code false}.
     */
    static @NotNull Dependency isFalse(BooleanListEntry gui) {
        return builder()
                .dependingOn(gui)
                .matching(false)
                .build();
    }
    
    /**
     * Generates a {@link EnumDependency} that checks if the {@code gui}'s value is one of the values provided.
     */
    @SafeVarargs
    static <T extends Enum<?>> @NotNull Dependency isValue(EnumListEntry<T> gui, T firstValue, T... otherValues) {
        return builder()
                .dependingOn(gui)
                .matching(firstValue)
                .matching(Arrays.stream(otherValues)
                        .map(EnumStaticCondition::new)
                        .toList())
                .build();
    }
    
    /**
     * Generates a {@link NumberDependency} that checks if the {@code gui}'s value is one of the values provided.
     */
    @SafeVarargs
    static <T extends Number & Comparable<T>> @NotNull Dependency isValue(NumberConfigEntry<T> gui, T firstValue, T... otherValues) {
        return builder()
                .dependingOn(gui)
                .matching(firstValue)
                .matching(Arrays.stream(otherValues)
                        .map(ComparativeStaticCondition::new)
                        .toList())
                .build();
    }
    
    /**
     * Generates a {@link NumberDependency} that compares the {@code gui}'s value to the given {@code value}, using the
     * provided {@code operator}. For example, <em>{@code gui_value > value}</em>.
     */
    // FIXME isValue is a horrible name here... useOperatorToCompareGuiValueToValue is a bit verbose though...
    static <T extends Number & Comparable<T>> @NotNull Dependency isValue(NumberConfigEntry<T> gui, ComparisonOperator operator, T value) {
        return builder()
                .dependingOn(gui)
                .matching(operator, value)
                .build();
    }
    
    /**
     * Generates a {@link BooleanListEntry} that compares the {@code gui}'s value to the given {@code otherGui}'s value.
     */
    static @NotNull Dependency matches(BooleanListEntry gui, BooleanListEntry otherGui) {
        return builder()
                .dependingOn(gui)
                .matching(otherGui)
                .build();
    }
    
    /**
     * Generates an {@link EnumDependency} that compares the {@code gui}'s value to the given {@code otherGui}'s value.
     */
    static <T extends Enum<?>> @NotNull Dependency matches(EnumListEntry<T> gui, EnumListEntry<T> otherGui) {
        return builder()
                .dependingOn(gui)
                .matching(otherGui)
                .build();
    }
    
    /**
     * Generates a {@link NumberDependency} that compares the {@code gui}'s value to the given {@code otherGui}'s value.
     */
    static <T extends Number & Comparable<T>> @NotNull Dependency matches(NumberConfigEntry<T> gui, NumberConfigEntry<T> otherGui) {
        return builder()
                .dependingOn(gui)
                .matching(otherGui)
                .build();
    }
    
    /**
     * Generates a {@link NumberDependency} that compares the {@code gui}'s value to the given {@code otherGui}'s value, using the
     * provided {@code operator}. For example, <em>{@code gui_value > other_gui_value}</em>.
     */
    static <T extends Number & Comparable<T>> @NotNull Dependency matches(NumberConfigEntry<T> gui, ComparisonOperator operator, NumberConfigEntry<T> otherGui) {
        return builder()
                .dependingOn(gui)
                .matching(operator, otherGui)
                .build();
    }
    
    /**
     * Generates a {@link GenericDependency} that compares the {@code gui}'s value to the given {@code otherGui}'s value.
     */
    static <T> @NotNull Dependency matches(ConfigEntry<T> gui, ConfigEntry<T> otherGui) {
        return builder()
                .dependingOnGeneric(gui)
                .matching(otherGui)
                .build();
    }
    
    /**
     * Generates a {@link DependencyGroup} that depends on all of its dependencies being met.
     * <br>
     * 
     * @param dependencies the dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull Dependency all(Dependency... dependencies) {
        return builder()
                .startGroup()
                .withRequirement(ALL)
                .withChildren(dependencies)
                .build();
    }
    
    /**
     * Generates a {@link DependencyGroup} that depends on none of its dependencies being met.
     * <br>
     * I.e. the group is unmet if any of its dependencies are met.
     *
     * @param dependencies the dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull Dependency none(Dependency... dependencies) {
        return builder()
                .startGroup()
                .withRequirement(NONE)
                .withChildren(dependencies)
                .build();
    }
    
    /**
     * Generates a {@link DependencyGroup} that depends on any of its dependencies being met.
     * <br>
     * I.e. the group is met if one or more of its dependencies are met. 
     *
     * @param dependencies the dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull Dependency any(Dependency... dependencies) {
        return builder()
                .startGroup()
                .withRequirement(ANY)
                .withChildren(dependencies)
                .build();
    }
    
    /**
     * Generates a {@link DependencyGroup} that depends on exactly one of its dependencies being met.
     * <br>
     * I.e. the group is met if precisely one dependency is met, however the group is unmet if more than one
     * (or less than one) are met.
     *
     * @param dependencies the dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull Dependency one(Dependency... dependencies) {
        return builder()
                .startGroup()
                .withRequirement(ONE)
                .withChildren(dependencies)
                .build();
    }
    
    /**
     * Checks if this dependency is currently met.
     *
     * @return whether the dependency is met
     */
    boolean check();
    
    /**
     * Get a short description of this dependency. For use by GUIs, e.g. {@link DependencyGroup} tooltips.
     *
     * @return a {@link Component} containing the description
     */
    @Nullable Component getShortDescription();
    
    /**
     * Generates a tooltip for this dependency.
     *
     * @return an {@link Optional} containing the tooltip, otherwise {@code Optional.empty()}.
     */
    @Nullable Component[] getTooltip(String effectKey);
    
    boolean hasTooltip();
    
    void setRequirement(GroupRequirement requirement);
    
    GroupRequirement getRequirement();
    
    void setDescription(Component description);
    
    void setTooltipProvider(Function<String, Component[]> tooltipProvider);
}
