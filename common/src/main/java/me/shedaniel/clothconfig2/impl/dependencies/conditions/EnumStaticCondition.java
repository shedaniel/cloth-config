package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EnumStaticCondition<T extends Enum<?>> extends AbstractStaticCondition<T> implements EqualityCondition<T> {
    
    public EnumStaticCondition(T value) {
        this(value, false);
    }
    
    public EnumStaticCondition(T value, boolean inverted) {
        super(value, inverted);
    }
    
    /**
     * @deprecated this method cannot return translatable text.
     *      {@link SelectionListEntry#getTextFor(Object)} is preferred over this method.
     * @see SelectionListEntry#getTextFor(Object)
     */
    @Deprecated
    @Override
    public Component getText(boolean inverted) {
        MutableComponent text = Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                Component.translatable("text.cloth-config.quoted", Component.literal(getValue().toString())));
    
        return inverted != inverted() ? Component.translatable("text.cloth-config.dependencies.conditions.not", text) : text;
    
    }
    
    
}
