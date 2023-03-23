package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.network.chat.Component;

public class EnumCondition<T extends Enum<?>> extends Condition<T> {
    public EnumCondition(T value) {
        super(value);
    }
    
    /**
     * @deprecated this method cannot return translatable text.
     *      {@link SelectionListEntry#getTextFor(Object)} is preferred over this method.
     * @see SelectionListEntry#getTextFor(Object)
     */
    @Deprecated
    @Override
    protected Component getTextInternal() {
        return Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                Component.translatable("text.cloth-config.quoted", Component.literal(getValue().toString())));
    }
}
