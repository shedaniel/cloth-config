package me.shedaniel.clothconfig2.api.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.requirements.Requirement;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * A mathematical comparison operator
 */
public enum ComparisonOperator implements Requirement<ComparisonOperator> {
    EQUAL("=="),
    NOT_EQUAL("!="),
    GREATER(">"),
    GREATER_EQUAL(">="),
    LESS("<"),
    LESS_EQUAL("<=");
    
    private final String symbol;
    
    ComparisonOperator(String symbol) {
        this.symbol = symbol;
    }
    
    public <T extends Comparable<T>> boolean compare(T left, T right) {
        int comparison = left.compareTo(right);
        return switch (this) {
            case EQUAL -> comparison == 0;
            case NOT_EQUAL -> comparison != 0;
            case GREATER -> comparison > 0;
            case GREATER_EQUAL -> comparison >= 0;
            case LESS -> comparison < 0;
            case LESS_EQUAL -> comparison <= 0;
        };
    }
    
    @Override
    public ComparisonOperator inverted(boolean inverted) {
        return switch (this) {
            case EQUAL -> NOT_EQUAL;
            case NOT_EQUAL -> EQUAL;
            case GREATER -> LESS_EQUAL;
            case GREATER_EQUAL -> LESS;
            case LESS -> GREATER_EQUAL;
            case LESS_EQUAL -> GREATER;
        };
    }
    
    // TODO consider making this a ConditionRequirement method?
    public static @Nullable ComparisonOperator startsWith(String string) {
        return Arrays.stream(values())
                .filter(value -> string.startsWith(value.symbol))
                .findAny()
                .orElse(null);
    }
    
    @Override
    public String toString() {
        return this.symbol;
    }
}
