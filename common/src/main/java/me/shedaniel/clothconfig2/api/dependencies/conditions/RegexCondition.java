package me.shedaniel.clothconfig2.api.dependencies.conditions;

import java.util.regex.Pattern;

public interface RegexCondition extends PredicateCondition<Pattern, String> {
    
    @Override
    default boolean predicate(Pattern pattern, String value) {
        return pattern.matcher(value).matches();
    }
}
