package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.BooleanCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Dependency {
    
    /**
     * Generates a {@link DependencyGroup} that depends on all of its dependencies being met.
     * <br>
     * 
     * @param dependencies the dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull DependencyGroup all(Dependency... dependencies) {
        return new DependencyGroup(DependencyGroup.Condition.ALL, dependencies);
    }
    /**
     * Generates a {@link DependencyGroup} that depends on all of its dependencies being met.
     * <br>
     * 
     * @param dependencies a {@link Collection} of dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull DependencyGroup all(Collection<Dependency> dependencies) {
        return all(dependencies.toArray(Dependency[]::new));
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
        return new DependencyGroup(DependencyGroup.Condition.NONE, dependencies);
    }
    /**
     * Generates a {@link DependencyGroup} that depends on none of its dependencies being met.
     * <br>
     * I.e. the group is unmet if any of its dependencies are met.
     *
     * @param dependencies a {@link Collection} of dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull DependencyGroup none(Collection<Dependency> dependencies) {
        return none(dependencies.toArray(Dependency[]::new));
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
        return new DependencyGroup(DependencyGroup.Condition.ANY, dependencies);
    }
    /**
     * Generates a {@link DependencyGroup} that depends on any of its dependencies being met.
     * <br>
     * I.e. the group is met if one or more of its dependencies are met. 
     *
     * @param dependencies a {@link Collection} of dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull DependencyGroup any(Collection<Dependency> dependencies) {
        return any(dependencies.toArray(Dependency[]::new));
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
        return new DependencyGroup(DependencyGroup.Condition.ONE, dependencies);
    }
    /**
     * Generates a {@link DependencyGroup} that depends on exactly one of its dependencies being met.
     * <br>
     * I.e. the group is met if precisely one dependency is met, however the group is unmet if more than one
     * (or less than one) are met.
     *
     * @param dependencies a {@link Collection} of dependencies to be included in the group 
     * @return the generated group
     */
    static @NotNull DependencyGroup one(Collection<Dependency> dependencies) {
        return one(dependencies.toArray(Dependency[]::new));
    }
    
    /**
     * Generates a {@link BooleanDependency}, dependent on {@code entry}'s value being {@code true}.
     * <br>
     * Any entry with this dependency will be <strong>hidden</strong> when the dependency is unmet.
     * 
     * @param entry the {@link BooleanListEntry} that is depended on.
     * @return the generated {@link BooleanDependency}.
     */
    static @NotNull BooleanDependency hiddenWhenNotMet(BooleanListEntry entry) {
        return hiddenWhenNotMet(entry, new BooleanCondition(true));
    }
    
    /**
     * Generates a {@link BooleanDependency}, dependent on {@code entry}'s value matching {@code condition}.
     * <br>
     * Any entry with this dependency will be <strong>hidden</strong> when the dependency is unmet.
     *
     * @param entry the {@link BooleanListEntry} that is depended on.
     * @param condition a {@link BooleanCondition} to check against {@code entry}
     * @return the generated {@link BooleanDependency}.
     */
    static @NotNull BooleanDependency hiddenWhenNotMet(BooleanListEntry entry, BooleanCondition condition) {
        BooleanDependency dependency = new BooleanDependency(entry, condition);
        dependency.hiddenWhenNotMet(true);
        return dependency;
    }
    
    /**
     * Generates a {@link BooleanDependency}, dependent on {@code entry}'s value being {@code true}.
     * <br>
     * Any entry with this dependency will be <strong>disabled</strong> (but still visible) when the dependency is unmet.
     *
     * @param entry the {@link BooleanListEntry} that is depended on.
     * @return the generated {@link BooleanDependency}.
     */
    static @NotNull BooleanDependency disabledWhenNotMet(BooleanListEntry entry) {
        return disabledWhenNotMet(entry, new BooleanCondition(true));
    }
    
    /**
     * Generates a {@link BooleanDependency}, dependent on {@code entry}'s value matching {@code condition}.
     * <br>
     * Any entry with this dependency will be <strong>disabled</strong> (but still visible) when the dependency is unmet.
     *
     * @param entry the {@link BooleanListEntry} that is depended on.
     * @param condition a {@link BooleanCondition} to check against {@code entry}
     * @return the generated {@link BooleanDependency}.
     */
    static @NotNull BooleanDependency disabledWhenNotMet(BooleanListEntry entry, BooleanCondition condition) {
        return new BooleanDependency(entry, condition);
    }
    
    /**
     * Generates a {@link SelectionDependency}, dependent on {@code entry}'s value matching one of the {@code conditions}.
     * <br>
     * Any entry with this dependency will be <strong>hidden</strong> when the dependency is unmet.
     *
     * @param entry the {@link EnumListEntry} that is depended on.
     * @param condition the expected value for {@code entry}
     * @param conditions optional additional values
     * @return the generated {@link SelectionDependency}.
     */
    @SafeVarargs //FIXME is generic varargs (T...) _actually_ safe or are we lying?
    static @NotNull <T extends Enum<?>> SelectionDependency<T> hiddenWhenNotMet(EnumListEntry<T> entry, T condition, T... conditions) {
        SelectionDependency<T> dependency = disabledWhenNotMet(entry, condition, conditions);
        dependency.hiddenWhenNotMet(true);
        return dependency;
    }
    
    /**
     * Generates a {@link SelectionDependency}, dependent on {@code entry}'s value matching one of the {@code conditions}.
     * <br>
     * Any entry with this dependency will be <strong>hidden</strong> when the dependency is unmet.
     *
     * @param entry the {@link EnumListEntry} that is depended on.
     * @param condition a {@link EnumCondition} to check against {@code entry}
     * @param conditions optional additional {@code condition}s
     * @return the generated {@link SelectionDependency}.
     */
    @SafeVarargs //FIXME is generic varargs (T...) _actually_ safe or are we lying?
    static @NotNull <T extends Enum<?>> SelectionDependency<T> hiddenWhenNotMet(EnumListEntry<T> entry, EnumCondition<T> condition, EnumCondition<T>... conditions) {
        SelectionDependency<T> dependency = disabledWhenNotMet(entry, condition, conditions);
        dependency.hiddenWhenNotMet(true);
        return dependency;
    }
    
    /**
     * Generates a {@link SelectionDependency}, dependent on {@code entry}'s value matching one of the {@code conditions}.
     * <br>
     * Any entry with this dependency will be <strong>disabled</strong> (but still visible) when the dependency is unmet.
     *
     * @param entry the {@link EnumListEntry} that is depended on.
     * @param condition the expected value for {@code entry}
     * @param conditions optional additional values
     * @return the generated {@link SelectionDependency}.
     */
    @SafeVarargs //FIXME is generic varargs (T...) _actually_ safe or are we lying?
    static @NotNull <T extends Enum<?>> SelectionDependency<T> disabledWhenNotMet(EnumListEntry<T> entry, T condition, T... conditions) {
        return disabledWhenNotMet(entry, new EnumCondition<>(condition))
                .withConditions(Arrays.stream(conditions).map(EnumCondition::new).toList());
    }
    
    /**
     * Generates a {@link SelectionDependency}, dependent on {@code entry}'s value matching one of the {@code conditions}.
     * <br>
     * Any entry with this dependency will be <strong>disabled</strong> (but still visible) when the dependency is unmet.
     *
     * @param entry the {@link EnumListEntry} that is depended on.
     * @param condition the expected value for {@code entry}
     * @param conditions optional additional values
     * @return the generated {@link SelectionDependency}.
     */
    @SafeVarargs //FIXME is generic varargs (T...) _actually_ safe or are we lying?
    static @NotNull <T extends Enum<?>> SelectionDependency<T> disabledWhenNotMet(EnumListEntry<T> entry, EnumCondition<T> condition, EnumCondition<T>... conditions) {
        SelectionDependency<T> dependency = new SelectionDependency<>(entry, condition);
        dependency.addConditions(List.of(conditions));
        return dependency;
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
    Component getShortDescription();
    
    /**
     * Generates a tooltip for this dependency.
     * 
     * @return an {@link Optional} containing the tooltip, otherwise {@code Optional.empty()}.
     */
    Optional<Component[]> getTooltip();
}
