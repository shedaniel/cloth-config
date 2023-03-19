package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

public class EnumCondition<T extends Enum<?>> extends Condition<T> {
    public EnumCondition(T value) {
        super(value);
    }
    
    @Override
    public boolean check(T value) {
        return inverted() != this.value.equals(value);
    }
    
    @Override
    public Component getText() {
        throw new IllegalStateException("EnumCondition.getText() is not implemented.");
    }
}
