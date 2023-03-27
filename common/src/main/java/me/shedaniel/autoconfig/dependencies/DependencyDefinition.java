package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A record defining a dependency to be built.
 * Can be declared using either an {@link ConfigEntry.Gui.EnableIf @EnableIf} or {@link ConfigEntry.Gui.ShowIf @ShowIf} annotation.
 *
 * @param i18n the absolute i18n key of the depended-on config entry
 * @param conditions an array of condition strings to be parsed
 * @see DependencyGroupDefinition
 */
@ApiStatus.Internal
record DependencyDefinition(String i18n, String[] conditions) {
    
    DependencyDefinition(@Nullable String i18nBase, ConfigEntry.Gui.EnableIf annotation) {
        this(i18nBase, annotation.value(), annotation.conditions());
    }
    DependencyDefinition(@Nullable String i18nBase, ConfigEntry.Gui.ShowIf annotation) {
        this(i18nBase, annotation.value(), annotation.conditions());
    }
    private DependencyDefinition(@Nullable String i18nBase, String i18nKey, String[] conditions) {
        this(DependencyManager.parseRelativeI18n(i18nBase, i18nKey), conditions);
    }
}
