package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class Condition<T> {
    
    protected final T value;
    
    private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
    
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
        setFlags(Flag.parseFlags(flags));
    }
    
    public final void setFlags() {
        setFlags(Flag.ALL);
    }
    
    public final void resetFlags(EnumSet<Flag> flags) {
        this.flags.removeAll(flags);
    }
    
    public final void resetFlags(String flags) {
        resetFlags(Flag.parseFlags(flags));
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
        flipFlags(Flag.parseFlags(flags));
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
    
        private static final EnumSet<Flag> ALL = EnumSet.allOf(Flag.class);
        private static final char FLAG_PREFIX = '{';
        private static final char FLAG_SUFFIX = '}';
    
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
        public static EnumSet<Flag> parseFlags(String flags) throws IllegalArgumentException {
            // Flags are case-insensitive  TODO is this dumb?
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
        
        /**
         * @param condition a string that may or may not begin with {@link Condition.Flag flags}
         * @return a {@link FlaggedString record} containing the parsed {@link Condition.Flag flags}
         *         and the remainder of the provided condition string
         * @throws IllegalArgumentException if the condition string begins a flags section without ending it
         * @see ConfigEntry.Gui.EnableIf#conditions() Public API documentation
         */
        public static FlaggedString fromConditionString(String condition) throws IllegalArgumentException {
            if (FLAG_PREFIX == condition.charAt(0)) {
                int end = condition.indexOf(FLAG_SUFFIX);
                if (end < 0)
                    throw new IllegalArgumentException("\"%1$s\" starts with the flag prefix '%2$s', but the flag suffix '%3$s' was not found. Did you mean \"%2$s%3$s%1$s\"?"
                            .formatted(condition, FLAG_PREFIX, FLAG_SUFFIX));
            
                String flags = condition.substring(1, end);
                String string = condition.substring(end + 1);
            
                return new FlaggedString(parseFlags(flags), string);
            }
            return new FlaggedString(EnumSet.noneOf(Flag.class), condition);
        }
    
        public record FlaggedString(EnumSet<Flag> flags, String condition) {}
    }
}
