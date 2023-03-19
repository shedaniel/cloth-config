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
    
    public abstract boolean check(T value);
    
    public abstract Component getText();
    
    public final boolean inverted() {
        return getFlags().contains(Flag.INVERTED);
    }
    
    public final boolean ignoreCase() {
        return getFlags().contains(Flag.IGNORE_CASE);
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
    
    // FIXME need to override equals() and hashCode() for ComplexDependency.equals() to work correctly
    
    public enum Flag {
    
        /**
         * When set, the condition check is inverted. I.e. {@link Condition#check(Object)} will return {@code true} if
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
