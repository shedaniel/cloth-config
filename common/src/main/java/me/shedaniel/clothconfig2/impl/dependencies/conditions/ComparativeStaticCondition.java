package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparativeCondition;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ComparisonOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class ComparativeStaticCondition<T extends Number & Comparable<T>> extends AbstractStaticCondition<T> implements ComparativeCondition<T> {

    private final ComparisonOperator operator;
    private final boolean integer;
    
    private int formatPrecision = -1;
    
    public ComparativeStaticCondition(@NotNull T value) {
        this(null, value);
    }
    
    public ComparativeStaticCondition(@Nullable ComparisonOperator operator, @NotNull T value) {
        this(operator, value, false);
    }
    
    public ComparativeStaticCondition(@Nullable ComparisonOperator operator, @NotNull T value, boolean inverted) {
        super(value, inverted);
        this.operator = operator == null ? ComparisonOperator.EQUAL : operator;
        
        Class<? extends Number> type = value.getClass();
        this.integer = type.isAssignableFrom(Long.class)
                    || type.isAssignableFrom(Integer.class)
                    || type.isAssignableFrom(Short.class)
                    || type.isAssignableFrom(BigInteger.class);
    }
    
    @Override
    public ComparisonOperator getRequirement() {
        return this.operator;
    }
    
    @Override
    public String getStringValue() {
        return integer ? getValue().toString() : formatDouble(getValue().doubleValue(), formatPrecision());
    }
    
    @Override
    public void setFormatPrecision(int places) {
        formatPrecision = places;
    }
    
    private static String formatDouble(double value, int places) {
        BigDecimal bigDecimal = new BigDecimal(value)
                .setScale(places, RoundingMode.HALF_UP);
        return NumberFormat.getInstance().format(bigDecimal);
    }
    
    @Override
    public int formatPrecision() {
        // if -1, calculate an appropriate precision for the scale of the number
        if (formatPrecision < 0) {
            long l = Math.abs(getValue().longValue());
            if (l == 0) {
                double d = Math.abs(getValue().doubleValue());
                if (d == 0)
                    return 0;
                if (d < 0.01)
                    return 4;
                if (d < 0.1)
                    return 3;
                return 2;
            }
            if (l > 99)
                return 0;
            if (l > 9)
                return 1;
            return 2;
        }
        return formatPrecision;
    }
    
}
