package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.EnumSet;

public abstract class StaticCondition<T> implements Condition<T> {
    
    private final T value;
    private final EnumSet<ConditionFlag> flags = EnumSet.noneOf(ConditionFlag.class);
    
    protected StaticCondition(T value) {
        
        this.value = value;
    }
    
    /**
         * Checks whether the provided value matches the condition's value, ignoring any {@link ConditionFlag flags}, such as
         * <em>'{@code !}' inversion</em>, which should be handled in {@link #check(Object) check(T)} instead.
         *
         * @param value the value to check against this condition
         * @return whether {@code value} satisfies this condition
         */
    protected boolean matches(T value) {
        return this.value.equals(value);
    }
    
    /**
     * Checks if the condition is met by the provided value.
     * 
     * @param value the value to check against this condition
     * @return whether {@code value} satisfies this condition
     */
    @Override
    public boolean check(T value) {
        return inverted() != matches(value);
    }
    
    public T getValue() {
        return value;
    }
    
    @Override
    public EnumSet<ConditionFlag> getFlags() {
        return flags;
    }
    
    protected abstract Component getTextInternal();
    
    @Override
    public Component getText(boolean inverted) {
        if (inverted != inverted())
            return Component.translatable("text.cloth-config.dependencies.conditions.not", getTextInternal());
        return getTextInternal();
    }
}
