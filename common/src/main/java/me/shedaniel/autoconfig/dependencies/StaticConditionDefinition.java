package me.shedaniel.autoconfig.dependencies;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Dependency.EnableIf;
import me.shedaniel.clothconfig2.api.dependencies.conditions.ConditionFlag;
import me.shedaniel.clothconfig2.impl.dependencies.conditions.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Function;

record StaticConditionDefinition(EnumSet<ConditionFlag> flags, String condition) {
    
    private static final char FLAG_PREFIX = '{';
    private static final char FLAG_SUFFIX = '}';
    
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
    
    BooleanCondition toBooleanCondition() {
        BooleanCondition condition = BooleanCondition.fromString(this.condition());
        condition.setFlags(this.flags());
        return condition;
    }
    
    StringCondition toStringCondition() {
        StringCondition condition = new StringCondition(this.condition());
        condition.setFlags(this.flags());
        return condition;
    }
    
    <T extends Enum<?>> EnumCondition<T> toEnumCondition(Class<T> type) {
        // Handle case-sensitivity
        boolean insensitive = this.flags().contains(ConditionFlag.IGNORE_CASE);
        String valueString = insensitive ? condition.strip().toLowerCase() : condition.strip();
        Function<T, String> toString = insensitive ? val -> val.toString().toLowerCase() : Object::toString;
    
        // Find a matching value in the possible values array
        T[] possibleValues = type.getEnumConstants();
        T value = Arrays.stream(possibleValues)
                .filter(val -> valueString.equals(toString.apply(val)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Invalid EnumCondition was defined: \"%s\"\nValid options: %s".formatted(this.condition(), Arrays.toString(possibleValues))));
    
        EnumCondition<T> condition = new EnumCondition<>(value);
        condition.setFlags(this.flags());
        return condition;
    }
    
    <T extends Number & Comparable<T>> NumberCondition<T> toNumberCondition(Class<T> type) {
        NumberCondition<T> condition = NumberCondition.fromString(type, this.condition());
        condition.setFlags(this.flags());
        return condition;
    }
    
    public <T> GenericCondition<T> toGenericCondition(Class<T> type) {
        GenericCondition<T> condition = new GenericCondition<>(type, this.condition());
        condition.setFlags(this.flags());
        return condition;
    }
}
