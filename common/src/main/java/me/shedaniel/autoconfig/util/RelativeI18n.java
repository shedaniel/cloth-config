package me.shedaniel.autoconfig.util;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import org.jetbrains.annotations.Nullable;

public class RelativeI18n {
    
    private static final char RELATIVE_STEP_PREFIX = '.';
    private static final char JOINER = '.';
    
    /**
     * Applies the given prefix to the i18n key. Effectively making i18n a child of the prefix.
     * 
     * @param prefix the parent part
     * @param i18n the child part
     * @return the resulting i18n key
     */
    public static String prefix(@Nullable String prefix, String i18n) {
        return prefix == null || prefix.isEmpty() ? i18n : prefix + JOINER + i18n;
    }
    
    /**
     * Gets the targeted i18n key, using {@code i18nBase} as a <em>base reference</em> if {@code  i18nKey} is relative.
     * 
     * @param i18nBase an absolute i18n key, to be used as the base reference point of the relative key
     * @param i18nKey either a relative or absolute i18n key
     * @return the absolute i18n key
     * @see ConfigEntry.Gui.EnableIf#value() Public API documentation
     */
    public static String parse(@Nullable String i18nBase, String i18nKey) {
        // Count how many "steps up" are at the start of the key string,
        int steps = 0;
        for (char c : i18nKey.toCharArray()) {
            if (RELATIVE_STEP_PREFIX == c) steps++;
            else break;
        }
        
        // Not a relative key
        if (steps < 1)
            return i18nKey;
        
        if (i18nBase == null)
            throw new IllegalArgumentException("Relative i18n key cannot be used without a base-reference");
        
        // Get the key without any "step" chars
        String key = i18nKey.substring(steps);
        String base = i18nBase;
        
        // Move `base` up one level for each "step" that was counted
        // Start from 1 since the first "step" is just indicating that the key is relative
        for (int i = 1; i < steps; i++) {
            base = parent(base);
            if (base == null)
                throw new IllegalArgumentException("Too many steps up (%d) relative to \"%s\"".formatted(steps, i18nBase));
        }
        
        return base + JOINER + key;
    }
    
    /**
     * Gets the parent of the provided i18n key. For example <em>{@code "a.good.child"}</em> returns <em>{@code "a.good"}</em>. 
     * @param i18n the key to get the parent of
     * @return the parent of {@code i18n}
     */
    public static String parent(String i18n) {
        int index = i18n.lastIndexOf(JOINER);
        
        // No parent to be found
        if (index < 1)
            return null;
        
        return i18n.substring(0, index);
    }
}
