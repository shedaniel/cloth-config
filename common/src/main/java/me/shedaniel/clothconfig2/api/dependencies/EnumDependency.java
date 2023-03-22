package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

public class EnumDependency<T extends Enum<?>> extends ComplexDependency<T, EnumCondition<T>, EnumListEntry<T>> {
    
    @ApiStatus.Internal
    @Deprecated
    public EnumDependency(EnumListEntry<T> entry) {
        super(entry);
    }
    
    @Override
    protected Component getConditionText(EnumCondition<T> condition) {
        return Component.translatable("text.cloth-config.quoted", getEntry().getTextFor(condition.getValue()));
    }
}
