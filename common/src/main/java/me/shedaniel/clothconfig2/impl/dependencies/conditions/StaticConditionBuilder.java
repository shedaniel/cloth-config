package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.ConfigEntry;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public class StaticConditionBuilder<T> extends SimpleConditionBuilder<T, StaticConditionBuilder<T>> {
    
    private final T staticValue;
    
    public StaticConditionBuilder(T value) {
        this.staticValue = value;
    }
    
    @Override
    protected Predicate<T> buildPredicate() {
        return this.staticValue::equals;
    }
    
    @Override
    public StaticConditionBuilder<T> describeUsing(ConfigEntry<T> gui) {
        if (gui instanceof BooleanListEntry booleanListEntry) {
            this.description = buildDescription(booleanListEntry);
            return this;
        }
        if (gui instanceof EnumListEntry<?> enumListEntry) {
            this.description = buildDescription(enumListEntry);
            return this;
        }
        return super.describeUsing(gui);
    }
    
    private <E extends Enum<?>> Component buildDescription(EnumListEntry<E> gui) {
        @SuppressWarnings("unchecked") E value = (E) this.staticValue;
        return setTo(gui.getTextFor(value));
    }
    
    private Component buildDescription(BooleanListEntry gui) {
        Boolean value = (Boolean) this.staticValue;
        return setTo(gui.getYesNoText(inverted != value));
    }
    
    @Override
    protected Component buildDescription() {
        if (this.staticValue instanceof Boolean bool)
            return buildDescription(bool);
        return buildDescription(this.staticValue);
    }
    
    private Component buildDescription(Boolean value) {
        return Component.translatable("text.cloth-config.dependencies.conditions.%s"
                .formatted(this.inverted != value ? "enabled" : "disabled"));
    }
    
    private Component buildDescription(T value) {
        return setTo(Component.literal(String.valueOf(value)));
    }
    
    private Component setTo(Component value) {
        Component description = Component.translatable("text.cloth-config.dependencies.conditions.set_to",
                Component.translatable("text.cloth-config.quoted", value));
    
        return this.inverted ? Component.translatable("text.cloth-config.dependencies.conditions.not", description) : description;
    }
}
