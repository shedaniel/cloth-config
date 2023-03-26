package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class Condition<T> {
    
    protected final T value;
    
    private final EnumSet<Flag> flags = Flag.NONE;
    
    protected Condition(T value) {
        this.value = value;
    }
    
    /**
     * Checks whether the provided value matches the condition's value, ignoring any {@link Flag flags}, such as
     * <em>'{@code !}' inversion</em>, which should be handled in {@link #check(Object) check(T)} instead.
     *
     * @param value the value to check against this condition
     * @return whether {@code value} satisfies this condition
     */
    protected boolean matches(T value) {
        return getValue().equals(value);
    }
    
    /**
     * Checks if the condition is met by the provided value.
     * 
     * @param value the value to check against this condition
     * @return whether {@code value} satisfies this condition
     */
    public final boolean check(T value) {
        return inverted() != matches(value);
    }
    
    protected abstract Component getTextInternal();
    
    public Component getText(boolean inverted) {
        if (inverted != inverted())
            return Component.translatable("text.cloth-config.dependencies.conditions.not", getTextInternal());
        return getTextInternal();
    }
    
    public final boolean inverted() {
        return getFlags().contains(Flag.INVERTED);
    }
    
    public final EnumSet<Flag> getFlags() {
        return flags;
    }
    
    public final void setFlags(EnumSet<Flag> flags) {
        this.flags.addAll(flags);
    }
    
    public final void setFlags(String flags) {
        setFlags(Flag.fromString(flags));
    }
    
    public final void setFlags() {
        setFlags(Flag.ALL);
    }
    
    public final void resetFlags(EnumSet<Flag> flags) {
        this.flags.removeAll(flags);
    }
    
    public final void resetFlags(String flags) {
        resetFlags(Flag.fromString(flags));
    }
    
    public final void resetFlags() {
        resetFlags(Flag.ALL);
    }
    
    public final void flipFlags(EnumSet<Flag> flags) {
        flags.forEach(flag -> {
            if (this.flags.contains(flag))
                this.flags.remove(flag);
            else
                this.flags.add(flag);
        });
    }
    
    public final void flipFlags(String flags) {
        flipFlags(Flag.fromString(flags));
    }
    
    public T getValue() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        return flags.hashCode() + 8 * value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;
        if (obj instanceof Condition<?> condition) {
            return this.value.equals(condition.value)
                && this.flags.equals(condition.flags);
        }
        return false;
    }
    
    public enum Flag {
    
        /**
         * When set, the condition check is inverted. I.e. {@link Condition#matches(Object)} will return {@code true} if
         * the check evaluated to {@code false} and vice-versa.
         */
        INVERTED('!'),
        
        /**
         * When set, text dependencies will ignore capitalization.
         */
        IGNORE_CASE('i');
    
        public static final EnumSet<Flag> ALL = EnumSet.allOf(Flag.class);
        public static final EnumSet<Flag> NONE = EnumSet.noneOf(Flag.class);
    
        private static final String VALID_FLAGS = Arrays.stream(Flag.values())
                .map(flag -> flag.symbol)
                .map(Objects::toString)
                .collect(Collectors.joining(" ", "\" ", " \""));
        
        private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile(Arrays.stream(Flag.values())
                .map(flag -> flag.symbol)
                .map(Object::toString)
                .map(Pattern::quote)
                .collect(Collectors.joining("|")));
    
        private final Character symbol;
    
        Flag(Character symbol) {
            this.symbol = symbol;
        }
    
        /**
         * Parse a {@link EnumSet set} of {@link Flag flags} from a {@link String} containing valid flag characters.
         * If any invalid character is found, an {@link IllegalArgumentException} will be thrown.
         * 
         * @param flags a {@link String} exclusively containing valid {@link Flag} symbols  
         * @return an {@link EnumSet} containing each {@link Flag} found in {@code flags}
         * @throws IllegalArgumentException if {@code flags} contains any unrecognised characters
         */
        public static EnumSet<Flag> fromString(String flags) throws IllegalArgumentException {
            // Flags are case-insensitive
            String symbols = flags.toLowerCase();
            
            // Map the flag string to an EnumSet
            EnumSet<Flag> flagSet = Arrays.stream(Flag.values())
                    .filter(flag -> symbols.indexOf(flag.symbol) >= 0)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(Flag.class)));
    
            // Check the entire string mapped to valid flags
            if (flagSet.size() != symbols.length()) {
                String invalid = VALID_SYMBOL_PATTERN.matcher(symbols).replaceAll("");
                throw new IllegalArgumentException("Unexpected flags \"%s\" in \"%s\", possible flags: %s"
                        .formatted(invalid, symbols, VALID_FLAGS));
            }
            
            return flagSet;
        }
    }
}
