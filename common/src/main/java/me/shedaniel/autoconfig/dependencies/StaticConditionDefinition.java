package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Dependency.EnableIf;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparativeCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.Condition;
import me.shedaniel.clothconfig2.api.dependencies.requirements.ComparisonOperator;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

record StaticConditionDefinition(EnumSet<ConditionFlag> flags, String condition) {
    
    private static final char FLAG_PREFIX = '{';
    private static final char FLAG_SUFFIX = '}';
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private static final List<String> STRING_PARSING_METHODS = List.of(
            "valueOf", "parseString", "parse", "fromString", "ofString", "from", "of");
    
    /**
     * @param condition a string that may or may not begin with {@link ConditionFlag flags}
     * @return a {@link StaticConditionDefinition record} containing the parsed {@link ConditionFlag flags}
     * and the remainder of the provided requirement string
     * @throws IllegalArgumentException if the requirement string begins a flags section without ending it
     * @see EnableIf#conditions() Public API documentation
     */
    static StaticConditionDefinition fromConditionString(String condition) throws IllegalArgumentException {
        if (FLAG_PREFIX == condition.charAt(0)) {
            int end = condition.indexOf(FLAG_SUFFIX);
            if (end < 0)
                throw new IllegalArgumentException("\"%1$s\" starts with the flag prefix '%2$s', but the flag suffix '%3$s' was not found. Did you mean \"%2$s%3$s%1$s\"?"
                        .formatted(condition, FLAG_PREFIX, FLAG_SUFFIX));
            
            String flags = condition.substring(1, end);
            String string = condition.substring(end + 1);
            
            return new StaticConditionDefinition(ConditionFlag.parseFlags(flags), string);
        }
        return new StaticConditionDefinition(EnumSet.noneOf(ConditionFlag.class), condition);
    }
    
    StaticConditionDefinition map(Function<String, String> mapper) {
        return new StaticConditionDefinition(this.flags(), mapper.apply(this.condition()));
    }
    
    private boolean inverted() {
        return this.flags().contains(ConditionFlag.INVERTED);
    }
    
    private boolean ignoreCase() {
        return this.flags().contains(ConditionFlag.IGNORE_CASE);
    }
    
    Condition<Boolean> toBooleanCondition() {
        // The switch expression is functionally equivalent to Boolean::parseBoolean,
        // but allows us to throw a RuntimeException
        String string = this.condition().strip().toLowerCase();
        boolean value = switch (string) {
            case "true" -> true;
            case "false" -> false;
            default ->
                    throw new IllegalStateException("Unexpected condition \"%s\" for Boolean dependency (expected \"true\" or \"false\").".formatted(string));
        };
        return new BooleanStaticCondition(value, this.inverted());
    }
    
    Condition<String> toStringCondition() {
        return new StringStaticCondition(this.condition(), this.ignoreCase(), this.inverted());
    }
    
    <T extends Enum<?>> Condition<T> toEnumCondition(Class<T> type) {
        // Handle case-sensitivity
        String valueString = this.ignoreCase() ? this.condition().strip().toLowerCase() : this.condition().strip();
        Function<T, String> toString = this.ignoreCase() ? val -> val.toString().toLowerCase() : Object::toString;
    
        // Find a matching value in the possible values array
        T[] possibleValues = type.getEnumConstants();
        T value = Arrays.stream(possibleValues)
                .filter(val -> valueString.equals(toString.apply(val)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid EnumCondition was defined: \"%s\"\nValid options: %s".formatted(this.condition(), Arrays.toString(possibleValues))));
    
        return new EnumStaticCondition<>(value, this.inverted());
    }
    
    /**
     * Build a {@code NumberCondition} represented by this definition.
     * <br><br>
     * The {@code type} parameter defines which number class is being dealt with. Currently, only the following
     * types are supported:
     *
     * <ul>
     *     <li>{@link Long}</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Short}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link Float}</li>
     * </ul>
     * <p>
     * The condition string can optionally begin with a mathematical {@link ComparisonOperator comparison operator}.
     * For example, the condition <em>"{@code >10}"</em> is true when the depended-on entry's value is greater than 10.
     *
     * <br><br>
     * <p>
     * Supported operators include:
     * <ul>
     *     <li>'{@code ==}' <em>equal to</em></li>
     *     <li>'{@code !=}' <em>not equal to</em></li>
     *     <li>'{@code >}' <em>greater than</em></li>
     *     <li>'{@code >=}' <em>greater than or equal to</em></li>
     *     <li>'{@code <}' <em>less than</em></li>
     *     <li>'{@code <=}' <em>less than or equal to</em></li>
     * </ul>
     * <p>
     * Any whitespace in the condition string will be completely ignored.
     *
     * @param type      a class defining what type of number the {@code NumberCondition} will handle
     * @param <T>       the type of {@link Number} the {@code NumberCondition} will deal with, must be {@link Comparable} with
     *                  other instances of type {@code T}
     * @return a {@code NumberCondition} representing the given {@code condition}
     * @throws NumberFormatException    if the {@code condition} string cannot be parsed into type {@code T}
     * @throws IllegalArgumentException if the given {@code type} ({@code T}) is not supported
     * @see ComparisonOperator
     */
    <T extends Number & Comparable<T>> ComparativeCondition<T> toNumberCondition(Class<T> type) {
        String stripped = WHITESPACE.matcher(this.condition()).replaceAll("");
        Optional<ComparisonOperator> optional = Optional.ofNullable(ComparisonOperator.startsWith(stripped));
    
        ComparisonOperator operator;
        String numberPart;
        if (optional.isPresent()) {
            operator = optional.get();
            numberPart = stripped.substring(operator.toString().length());
        } else {
            operator = ComparisonOperator.EQUAL;
            numberPart = stripped;
        }
    
        T number;
        if (type.isAssignableFrom(Long.class))
            number = type.cast(Long.parseLong(numberPart));
        else if (type.isAssignableFrom(Integer.class))
            number = type.cast(Integer.parseInt(numberPart));
        else if (type.isAssignableFrom(Short.class))
            number = type.cast(Short.parseShort(numberPart));
        else if (type.isAssignableFrom(Double.class))
            number = type.cast(Double.parseDouble(numberPart));
        else if (type.isAssignableFrom(Float.class))
            number = type.cast(Float.parseFloat(numberPart));
        else
            throw new IllegalArgumentException("Unsupported Number type \"%s\"".formatted(type.getSimpleName()));
    
        return new ComparativeStaticCondition<>(operator, number, this.inverted());
    }
    
    public <T> Condition<T> toGenericCondition(Class<T> type) {
        // May throw NoStringParserAvailableException if a supported string parsing method isn't found on `type`
        return new GenericStaticCondition<>(getStringParser(type).apply(this.condition()), this.inverted());
    }
    
    private static <T> Function<String, T> getStringParser(Class<T> type) throws NoStringParserAvailableException {
        Method parser = STRING_PARSING_METHODS.stream()
                .map(name -> getOptionalMethod(type, name, String.class))
                .flatMap(Optional::stream)
                .filter(method -> method.canAccess(null))
                .filter(method -> type.isAssignableFrom(method.getReturnType()))
                .findFirst()
                .orElseThrow(() -> new NoStringParserAvailableException("%s must implement one of the following public static methods: %s"
                        .formatted(type.getSimpleName(), STRING_PARSING_METHODS)));
        
        // Return a function that calls the method and casts the result
        return string -> {
            try {
                return type.cast(parser.invoke(null, string));
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                throw new IllegalStateException(e);
            }
        };
        
    }
    
    private static Optional<Method> getOptionalMethod(Class<?> type, String name, Class<?>... params) {
        try {
            return Optional.of(type.getMethod(name, params));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }
    
    public static class NoStringParserAvailableException extends RuntimeException {
        private NoStringParserAvailableException(String message) {
            super(message);
        }
    }
}
