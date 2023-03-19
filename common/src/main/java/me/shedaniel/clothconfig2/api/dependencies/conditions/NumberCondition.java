package me.shedaniel.clothconfig2.api.dependencies.conditions;

import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Optional;

public class NumberCondition extends Condition<Number> {
    
    private final Operator operator;
    
    public NumberCondition(Operator operator, Number value) {
        super(value);
        this.operator = operator;
        
        if (Double.isNaN(value.doubleValue()))
            throw new NumberFormatException("NumberCondition values must be convertible to a valid double");
    }
    
    public static NumberCondition fromString(String string) {
        // FIXME string.replaceAll("\\s", ""); is more concise, but less efficient
        //  consider using it instead to improve readability?
        StringBuilder sb = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (!Character.isWhitespace(c))
                sb.append(c);
        }
        String stripped = sb.toString();
        
        Operator operator;
        String numberPart;
        Optional<Operator> optional = Arrays.stream(Operator.values())
                .filter(value -> stripped.startsWith(value.toString()))
                .findAny();
        
        if (optional.isPresent()) {
            operator = optional.get();
            numberPart = stripped.substring(operator.toString().length());
        } else {
            operator = Operator.EQUALS;
            numberPart = stripped;
        }
        
        // parseDouble() may throw NumberFormatException
        double number = Double.parseDouble(numberPart);
        
        return new NumberCondition(operator, number);
    }
    
    @Override
    public boolean check(Number value) {
        double value1 = this.value.doubleValue();
        double value2 = value.doubleValue();
        boolean check = switch (this.operator) {
            case EQUALS -> value1 == value1;
            case NOT -> value1 != value2;
            case GREATER -> value1 > value2;
            case GREATER_EQUAL -> value1 >= value2;
            case LESS -> value1 < value2;
            case LESS_EQUAL -> value1 <= value2;
            case APPROX -> Math.floor(value1) == Math.floor(value2)
                           || Math.floor(value1) == Math.ceil(value2)
                           || Math.ceil(value1) == Math.floor(value2)
                           || Math.ceil(value1) == Math.ceil(value2);
        };
        return inverted() != check;
    }
    
    @Override
    public Component getText() {
        String i18n = "text.cloth-config.dependencies.conditions.%s".formatted(this.operator.name().toLowerCase());
        return Component.translatable(i18n, this.value.doubleValue());
    }
    
    protected enum Operator {
        EQUALS("=="),
        NOT("!="),
        GREATER(">"),
        GREATER_EQUAL(">="),
        LESS("<"),
        LESS_EQUAL("<="),
        APPROX("~=");
        
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
