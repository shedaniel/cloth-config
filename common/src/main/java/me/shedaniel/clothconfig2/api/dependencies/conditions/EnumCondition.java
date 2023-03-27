package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Function;

public class EnumCondition<T extends Enum<?>> extends Condition<T> {
    public EnumCondition(T value) {
        super(value);
    }
    
    /**
     * Build an {@code EnumCondition} represented by the given {@code condition} string.
     *
     * @param type a class defining what type of enum the {@code EnumCondition} will handle
     * @param condition a string that can be parsed into type {@code T}
     * @param <T> the type of {@link Enum} the {@code EnumCondition} will deal with
     * @return an {@code EnumCondition} representing the given {@code condition}
     * @throws IllegalArgumentException if the condition cannot be parsed into {@code type} ({@code T})  
     */
    public static <T extends Enum<?>> @NotNull EnumCondition<T> fromString(@NotNull Class<T> type, @NotNull String condition) throws IllegalArgumentException {
        return fromString(type, condition, false);
    }
    
    /**
     * Build an {@code EnumCondition} represented by the given {@code condition} string.
     *
     * @param type a class defining what type of enum the {@code EnumCondition} will handle
     * @param condition a string that can be parsed into type {@code T}
     * @param insensitive whether to use a case-insensitive match
     * @param <T> the type of {@link Enum} the {@code EnumCondition} will deal with
     * @return an {@code EnumCondition} representing the given {@code condition}
     * @throws IllegalArgumentException if the condition cannot be parsed into {@code type} ({@code T})  
     */
    public static <T extends Enum<?>> @NotNull EnumCondition<T> fromString(@NotNull Class<T> type, @NotNull String condition, boolean insensitive) throws IllegalArgumentException {
        String stripped = insensitive ? condition.strip().toLowerCase() : condition.strip();
        Function<T, String> toString = insensitive ? Object::toString : val -> val.toString().toLowerCase();
    
        // List of valid values for the depended-on EnumListEntry
        T[] possibleValues = type.getEnumConstants();
        
        return new EnumCondition<>(Arrays.stream(possibleValues)
                .filter(val -> stripped.equals(toString.apply(val)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid EnumCondition was defined: \"%s\"\nValid options: %s".formatted(condition, Arrays.toString(possibleValues)))));
    }
    
    public static <T extends Enum<?>> @NotNull EnumCondition<T> fromConditionString(@NotNull Class<T> type, @NotNull String condition) throws IllegalArgumentException {
        Flag.FlaggedString record = Flag.fromConditionString(condition);
        EnumCondition<T> enumCondition = fromString(type, record.condition(), record.flags().contains(Flag.IGNORE_CASE));
        enumCondition.setFlags(record.flags());
        return enumCondition;
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
