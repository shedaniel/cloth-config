package me.shedaniel.clothconfig2.api.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.network.chat.Component;

public class EnumDependency<T extends Enum<?>> extends ComplexDependency<T, EnumCondition<T>, EnumListEntry<T>, EnumDependency<T>> {
    
    EnumDependency(EnumListEntry<T> entry, EnumCondition<T> condition) {
        super(entry);
        setCondition(condition);
    }
    
    @Override
    public EnumDependency<T> withSimpleCondition(T value) {
        addCondition(new EnumCondition<>(value));
        return this;
    }
    
    @Override
    protected Component getConditionText(EnumCondition<T> condition) {
        return Component.translatable("text.cloth-config.quoted", getEntry().getTextFor(condition.getValue()));
    }
}
