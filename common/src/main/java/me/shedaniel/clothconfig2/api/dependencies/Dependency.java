package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Dependency {
    
    /**
     * Generates a {@link BooleanDependency}, dependent on {@code entry}'s value being {@code true}.
     * <br>
     * Any entry with this dependency will be <strong>hidden</strong> when the dependency is unmet.
     * 
     * @param entry the {@link BooleanListEntry} that is depended on.
     * @return the generated {@link BooleanDependency}.
     */
    static @NotNull BooleanDependency hiddenWhenNotMet(BooleanListEntry entry) {
        return hiddenWhenNotMet(entry, true);
    }
    
    /**
     * Generates a {@link BooleanDependency}, dependent on {@code entry}'s value matching {@code condition}.
     * <br>
     * Any entry with this dependency will be <strong>hidden</strong> when the dependency is unmet.
     *
     * @param entry the {@link BooleanListEntry} that is depended on.
     * @param condition the expected value for {@code entry}
     * @return the generated {@link BooleanDependency}.
     */
    static @NotNull BooleanDependency hiddenWhenNotMet(BooleanListEntry entry, boolean condition) {
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
        return disabledWhenNotMet(entry, true);
    }
    
    /**
     * Generates a {@link BooleanDependency}, dependent on {@code entry}'s value matching {@code condition}.
     * <br>
     * Any entry with this dependency will be <strong>disabled</strong> (but still visible) when the dependency is unmet.
     *
     * @param entry the {@link BooleanListEntry} that is depended on.
     * @param condition the expected value for {@code entry}
     * @return the generated {@link BooleanDependency}.
     */
    static @NotNull BooleanDependency disabledWhenNotMet(BooleanListEntry entry, boolean condition) {
        return new BooleanDependency(entry, condition);
    }
    
    /**
     * Generates a {@link SelectionDependency}, dependent on {@code entry}'s value matching one of the {@code conditions}.
     * <br>
     * Any entry with this dependency will be <strong>hidden</strong> when the dependency is unmet.
     *
     * @param entry the {@link SelectionListEntry} that is depended on.
     * @param condition the expected value for {@code entry}
     * @param conditions optional additional values
     * @return the generated {@link SelectionDependency}.
     */
    @SafeVarargs //FIXME is generic varargs (T...) _actually_ safe or are we lying?
    static @NotNull <T> SelectionDependency<T> hiddenWhenNotMet(SelectionListEntry<T> entry, T condition, T... conditions) {
        SelectionDependency<T> dependency = new SelectionDependency<>(entry, condition, conditions);
        dependency.hiddenWhenNotMet(true);
        return dependency;
    }
    
    /**
     * Generates a {@link SelectionDependency}, dependent on {@code entry}'s value matching one of the {@code conditions}.
     * <br>
     * Any entry with this dependency will be <strong>disabled</strong> (but still visible) when the dependency is unmet.
     *
     * @param entry the {@link SelectionListEntry} that is depended on.
     * @param condition the expected value for {@code entry}
     * @param conditions optional additional values
     * @return the generated {@link SelectionDependency}.
     */
    @SafeVarargs //FIXME is generic varargs (T...) _actually_ safe or are we lying?
    static @NotNull <T> SelectionDependency<T> disabledWhenNotMet(SelectionListEntry<T> entry, T condition, T... conditions) {
        return new SelectionDependency<>(entry, condition, conditions);
    }
    
    /**
     * Checks if this dependency is currently met.
     * 
     * @return whether the dependency is met
     */
    boolean check();
    
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
     * Generates a tooltip for this dependency.
     * 
     * @return an {@link Optional} containing the tooltip, otherwise {@code Optional.empty()}.
     */
    Optional<Component[]> getTooltip();
}
