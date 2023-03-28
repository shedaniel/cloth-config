package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * A dependency that compares a given config entry's value to that of other config entries.
 * 
 * @param <T> the type handled by the config entries 
 */
public class ComparatorDependency<T> extends ConfigEntryDependency<T, ConfigEntry<T>> {
    ComparatorDependency(ConfigEntry<T> entry) {
        super(entry);
        // TODO consider allowing multi-to-multi matching?
        // TODO consider using DependencyGroup.Condition to allow ALL/ANY/NONE etc?
    }
    
    @Override
    public Component getShortDescription(boolean inverted) {
        return super.getShortDescription(inverted);
    }
    
    @Override
    public Optional<Component[]> getTooltip(boolean inverted) {
        return super.getTooltip(inverted);
    }
}
