package me.shedaniel.clothconfig2.api.entries;

import me.shedaniel.clothconfig2.api.dependencies.Dependency;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;

public interface ConfigEntry<T> {
    
    T getValue();
    
    /**
     * Get the entry's dependency.
     *
     * @return the {@link Dependency}
     */
    Dependency getDependency();
    
    /**
     * Sets the entry's dependency. Whenever the dependency is unmet, the entry will be disabled.
     * <br>
     * Passing in a {@code null} value will remove the entry's dependency.
     *
     * @param dependency the new dependency. 
     */
    void setDependency(@Nullable Dependency dependency);
    
    Component getFieldName();
    
    default String getI18nKey() {
        Component component = getFieldName();
        return component.getContents() instanceof TranslatableContents translatable ?
                translatable.getKey() : component.getString();
    }
    
    default Class<T> getType() {
        //noinspection unchecked
        return (Class<T>) getValue().getClass();
    }
}
