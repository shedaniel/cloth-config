package me.shedaniel.clothconfig2.impl.dependencies;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EnumCondition;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EnumDependency<T extends Enum<?>> extends ConfigEntryDependency<T, EnumListEntry<T>, EnumCondition<T>> {
    
    EnumDependency(EnumListEntry<T> entry) {
        super(entry);
    }
    
    @Override
    protected Component getConditionText(EnumCondition<T> condition, boolean inverted) {
        MutableComponent text = Component.translatable("text.cloth-config.quoted", getElement().getTextFor(condition.getValue()));
        if (condition.inverted())
            text = Component.translatable("text.cloth-config.dependencies.conditions.not", text);
        return text;
    }
}