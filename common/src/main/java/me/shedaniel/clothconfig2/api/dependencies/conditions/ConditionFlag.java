package me.shedaniel.clothconfig2.api.dependencies.conditions;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum ConditionFlag {
    
    /**
     * When set, the condition check is inverted. I.e. {@link Condition#matches(Object)} will return {@code true} if
     * the check evaluated to {@code false} and vice-versa.
     */
    INVERTED('!'),
    
    /**
     * When set, text dependencies will ignore capitalization.
     */
    IGNORE_CASE('i');
    
    static final EnumSet<ConditionFlag> ALL = EnumSet.allOf(ConditionFlag.class);
    
    private static final String VALID_FLAGS = Arrays.stream(ConditionFlag.values())
            .map(flag -> flag.symbol)
            .map(Objects::toString)
            .collect(Collectors.joining(" ", "\" ", " \""));
    
    private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile(Arrays.stream(ConditionFlag.values())
            .map(flag -> flag.symbol)
            .map(Object::toString)
            .map(Pattern::quote)
            .collect(Collectors.joining("|")));
    
    private final Character symbol;
    
    ConditionFlag(Character symbol) {
        this.symbol = symbol;
    }
    
    /**
     * Parse a {@link EnumSet set} of {@link ConditionFlag flags} from a {@link String} containing valid flag characters.
     * If any invalid character is found, an {@link IllegalArgumentException} will be thrown.
     *
     * @param flags a {@link String} exclusively containing valid {@link ConditionFlag} symbols
     * @return an {@link EnumSet} containing each {@link ConditionFlag} found in {@code flags}
     * @throws IllegalArgumentException if {@code flags} contains any unrecognised characters
     */
    public static EnumSet<ConditionFlag> parseFlags(String flags) throws IllegalArgumentException {
        // Flags are case-insensitive  TODO is this dumb?
        String symbols = flags.toLowerCase();
        
        // Map the flag string to an EnumSet
        EnumSet<ConditionFlag> flagSet = Arrays.stream(ConditionFlag.values())
                .filter(flag -> symbols.indexOf(flag.symbol) >= 0)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(ConditionFlag.class)));
        
        // Check the entire string mapped to valid flags
        if (flagSet.size() != symbols.length()) {
            String invalid = VALID_SYMBOL_PATTERN.matcher(symbols).replaceAll("");
            throw new IllegalArgumentException("Unexpected flags \"%s\" in \"%s\", possible flags: %s"
                    .formatted(invalid, symbols, VALID_FLAGS));
        }
        
        return flagSet;
    }
    
}
