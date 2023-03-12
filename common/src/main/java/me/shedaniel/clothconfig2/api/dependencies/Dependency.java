package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Dependency<T, E extends AbstractConfigEntry<T>> {
    
    private final E entry;
    private final Collection<T> conditions = new ArrayList<>();
    
    private boolean shouldHide = false;
    
    protected Dependency(E entry) {
        this.entry = entry;
    }
    
    /**
     * Generates a {@link BooleanDependency}, dependent on {@code entry}'s value being {@code true}.
     * <br>
     * Any entry with this dependency will be <strong>hidden</strong> when the dependency is unmet.
     * 
     * @param entry the {@link BooleanListEntry} that is depended on.
     * @return the generated {@link BooleanDependency}.
     */
    public static @NotNull BooleanDependency hiddenWhenNotMet(BooleanListEntry entry) {
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
    public static @NotNull BooleanDependency hiddenWhenNotMet(BooleanListEntry entry, boolean condition) {
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
    public static @NotNull BooleanDependency disabledWhenNotMet(BooleanListEntry entry) {
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
    public static @NotNull BooleanDependency disabledWhenNotMet(BooleanListEntry entry, boolean condition) {
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
    @SafeVarargs //FIXME is generic vargs (T...) _actually_ safe or are we lying?
    public static @NotNull <T> SelectionDependency<T> hiddenWhenNotMet(SelectionListEntry<T> entry, T condition, T... conditions) {
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
    @SafeVarargs //FIXME is generic vargs (T...) _actually_ safe or are we lying?
    public static @NotNull <T> SelectionDependency<T> disabledWhenNotMet(SelectionListEntry<T> entry, T condition, T... conditions) {
        return new SelectionDependency<>(entry, condition, conditions);
    }
    
    /**
     * @return whether <strong>any</strong> of conditions are met
     */
    public boolean check() {
        T value = getEntry().getValue();
        return getConditions().stream().anyMatch(value::equals);
    }
    
    /**
     * Clears any conditions already defined and adds the condition provided
     * <br>
     * You can use {@code addCondition()} to add condition(s) without removing existing conditions.
     * 
     * @param condition the new condition to be set
     */
    public final void setCondition(T condition) {
        conditions.clear();
        conditions.add(condition);
    }
    
    /**
     * Adds one or more conditions to the dependency. If any condition matches the entry's value,
     * then the dependency is met.
     * <br>
     * Unlike {@code setCondition()}, existing conditions are not removed.
     *
     * @param conditions the conditions to be added
     */
    @SafeVarargs //FIXME is this actually safe from heap pollution? T... aka Object[] seems okay-ish?
    public final void addCondition(T... conditions) {
        addConditions(Arrays.asList(conditions));
    }
    
    /**
     * Adds one or more conditions to the dependency. If any condition matches the entry's value,
     * then the dependency is met.
     * <br>
     * Unlike {@code setCondition()}, existing conditions are not removed.
     *
     * @param conditions a {@link Collection} of conditions to be added
     */
    public final void addConditions(Collection<T> conditions) {
        this.conditions.addAll(conditions);
    }
    
    /**
     * @return the Config Entry that is depended on
     */
    public final E getEntry() {
        return entry;
    }
    
    /**
     * Get the dependency's conditions.
     * 
     * @return a {@link Collection} containing the dependency's conditions
     */
    public final Collection<T> getConditions() {
        return conditions;
    }
    
    /**
     * @return whether entries with this dependency should hide when this dependency is unmet, instead of simply being disabled.
     */
    public final boolean hiddenWhenNotMet() {
        return shouldHide;
    }
    
    /**
     * Sets whether entries with this dependency should hide when this dependency is unmet, instead of simply being disabled.
     * 
     * @param shouldHide whether dependant entries should hide
     */
    public final void hiddenWhenNotMet(boolean shouldHide) {
        this.shouldHide = shouldHide;
    }
    
    /**
     * Gets the localised human-readable text for a dependency's condition
     * 
     * @param condition the condition to get text for
     * @return the localised human-readable text
     */
    protected abstract Component getConditionText(T condition);
    
    /**
     * Generates a tooltip for this dependency, if the dependency is currently not met.
     * <br>
     * For example <em>Depends on "Some Config Entry" being set to "YES".</em>
     * 
     * @return an {@link Optional} containing the tooltip, otherwise {@code Optional.empty()}.
     */
    public Optional<Component[]> getTooltip() {
        // Only generate a tooltip if the dependency is unmet
        // TODO consider always showing the tooltip, even when the dependency is met?
        if (check())
            return Optional.empty();
        
        Collection<T> conditions = getConditions();
        if (conditions.isEmpty())
            // FIXME can we catch this illegal state earlier, e.g. during construction?
            // Probably best to keep this assertion here anyway, just in case anything ever gets broken.
            throw new IllegalStateException("Expected at least one condition to be defined");
        
        // Get the name of the depended-on entry, and style it bold.
        Component dependencyName = MutableComponent.create(getEntry().getFieldName().getContents())
                .withStyle(ChatFormatting.BOLD);
        
        // Get the text for each condition, again styled bold.
        List<MutableComponent> conditionTexts = conditions.stream()
                .distinct()
                .map(this::getConditionText)
                .map(text -> MutableComponent.create(text.getContents()))
                .map(text -> text.withStyle(ChatFormatting.BOLD))
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
            conditionTexts.forEach(text -> tooltip.add(Component.translatable("text.cloth-config.dependencies.list_entry", text)));
        }
        
        return Optional.of(tooltip.toArray(new Component[0]));
    }
}
