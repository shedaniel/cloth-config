package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class NumberCondition<T extends Number & Comparable<T>> extends Condition<T> implements Comparable<T> {

    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    
    private final Operator operator;
    private final boolean integer;
    
    public NumberCondition(T value) {
        this(Operator.EQUALS, value);
    }

    public NumberCondition(Operator operator, T value) {
        super(value);
        this.operator = operator;
        
        Class<? extends Number> type = value.getClass();
        this.integer = type.isAssignableFrom(Long.class) || type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Short.class);
    }
    
    /**
     * Build a {@code NumberCondition} represented by the given {@code condition} string.
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
     * 
     * The condition string can optionally begin with a mathematical {@link Operator comparison operator}.
     * For example, the condition <em>"{@code >10}"</em> is true when the depended-on entry's value is greater than 10.
     * 
     * <br><br>
     * 
     * Supported operators include:
     * <ul>
     *     <li>'{@code ==}' <em>equal to</em></li>
     *     <li>'{@code !=}' <em>not equal to</em></li>
     *     <li>'{@code >}' <em>greater than</em></li>
     *     <li>'{@code >=}' <em>greater than or equal to</em></li>
     *     <li>'{@code <}' <em>less than</em></li>
     *     <li>'{@code <=}' <em>less than or equal to</em></li>
     * </ul>
     * 
     * Any whitespace in the condition string will be completely ignored.
     * 
     * @param type a class defining what type of number the {@code NumberCondition} will handle
     * @param condition a string that can be parsed into type {@code T}, optionally prefixed with a {@link Operator comparison operator} 
     * @param <T> the type of {@link Number} the {@code NumberCondition} will deal with, must be {@link Comparable} with
     *           other instances of type {@code T}
     * @return a {@code NumberCondition} representing the given {@code condition}
     * @throws NumberFormatException if the {@code condition} string cannot be parsed into type {@code T}
     * @throws IllegalArgumentException if the given {@code type} ({@code T}) is not supported
     * @see Operator
     */
    public static <T extends Number & Comparable<T>> @NotNull NumberCondition<T> fromString(@NotNull Class<T> type, @NotNull String condition) throws IllegalArgumentException {
        String stripped = WHITESPACE.matcher(condition).replaceAll("");
        Optional<Operator> optional = Arrays.stream(Operator.values())
                .filter(value -> stripped.startsWith(value.toString()))
                .findAny();
        
        Operator operator;
        String numberPart;
        if (optional.isPresent()) {
            operator = optional.get();
            numberPart = stripped.substring(operator.toString().length());
        } else {
            operator = Operator.EQUALS;
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

        return new NumberCondition<>(operator, number);
    }
    
    @Override
    public boolean check(T value) {
        boolean check = switch (this.operator) {
            case EQUALS        -> 0 == compareTo(value);
            case NOT           -> 0 != compareTo(value);
            case GREATER       -> 0 >  compareTo(value);
            case GREATER_EQUAL -> 0 >= compareTo(value);
            case LESS          -> 0 <  compareTo(value);
            case LESS_EQUAL    -> 0 <= compareTo(value);
        };
        return inverted() != check;
    }
    
    @Override
    @Contract(pure = true)
    public int compareTo(@NotNull T value) {
        return this.value.compareTo(value);
    }
    
    @Override
    public Component getText() {
        return Component.translatable(
                "text.cloth-config.dependencies.conditions.%s".formatted(this.operator.name().toLowerCase()),
                // FIXME rounding floats to 2 places may not be appropriate for some config-entry ranges
                integer ? this.value.toString() : formatDouble(this.value.doubleValue(), 2));
    }
    
    private static String formatDouble(double value, int places) {
        BigDecimal bigDecimal = new BigDecimal(value)
                .setScale(places, RoundingMode.HALF_UP);
        return NumberFormat.getInstance().format(bigDecimal);
    }
    
    /**
     * A mathematical comparison operator
     */
    public enum Operator {
        EQUALS("=="),
        NOT("!="),
        GREATER(">"),
        GREATER_EQUAL(">="),
        LESS("<"),
        LESS_EQUAL("<=");
        
        private final String symbol;
        
        Operator(String symbol) {
            this.symbol = symbol;
        }
        
        @Override
        public String toString() {
            return this.symbol;
        }
    }
}
