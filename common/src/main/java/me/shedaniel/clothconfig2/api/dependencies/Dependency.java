package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.NumberConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import me.shedaniel.clothconfig2.impl.dependencies.*;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static me.shedaniel.clothconfig2.impl.dependencies.DependencyGroup.Condition.*;

public interface Dependency {
    
    static BooleanDependencyBuilder builder(BooleanListEntry gui) {
        return new BooleanDependencyBuilder(gui);
    }
    
    static <T extends Enum<?>> EnumDependencyBuilder<T> builder(EnumListEntry<T> gui) {
        return new EnumDependencyBuilder<>(gui);
    }
    
    static <T extends Number & Comparable<T>> NumberDependencyBuilder<T> builder(NumberConfigEntry<T> gui) {
        return new NumberDependencyBuilder<>(gui);
    }
    
    static DependencyGroupBuilder groupBuilder() {
        return new DependencyGroupBuilder();
    }
    
    /**
     * Generates a {@link DependencyGroup} that depends on all of its dependencies being met.
     * <br>
     * 
     * @param dependencies the dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull DependencyGroup all(Dependency... dependencies) {
        return groupBuilder()
                .withCondition(ALL)
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
    static @NotNull DependencyGroup none(Dependency... dependencies) {
        return groupBuilder()
                .withCondition(NONE)
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
    static @NotNull DependencyGroup any(Dependency... dependencies) {
        return groupBuilder()
                .withCondition(ANY)
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
    static @NotNull DependencyGroup one(Dependency... dependencies) {
        return groupBuilder()
                .withCondition(ONE)
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
     * Checks if this dependency is currently unmet and an entry with this dependency should be hidden.
     *
     * @return whether dependent entries should be hidden
     */
    default boolean hidden() {
        return !check() && hiddenWhenNotMet();
    }
    
    /**
     * @return whether entries with this dependency should hide when this dependency is unmet, instead of simply being disabled.
     */
    boolean hiddenWhenNotMet();
    
    /**
     * Sets whether entries with this dependency should hide when this dependency is unmet, instead of simply being disabled.
     * 
     * @param shouldHide whether dependant entries should hide
     */
    void hiddenWhenNotMet(boolean shouldHide);
    
    /**
     * Get a short description of this dependency. For use by GUIs, e.g. {@link DependencyGroup} tooltips.
     * 
     * @return a {@link Component} containing the description
     */
    default Component getShortDescription() {
        return getShortDescription(false);
    }
    
    @ApiStatus.Internal
    Component getShortDescription(boolean inverted);
    
    /**
     * Generates a tooltip for this dependency.
     * 
     * @return an {@link Optional} containing the tooltip, otherwise {@code Optional.empty()}.
     */
    default Optional<Component[]> getTooltip() {
        return getTooltip(false);
    }
    
    @ApiStatus.Internal
    Optional<Component[]> getTooltip(boolean inverted);
}
