package me.shedaniel.clothconfig2.impl.dependencies.conditions;

import me.shedaniel.clothconfig2.api.dependencies.conditions.EqualityCondition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class GenericStaticCondition<T> extends AbstractStaticCondition<T> implements EqualityCondition<T> {
    
    private static final List<String> SUPPORTED_PARSING_METHODS = List.of(
            "valueOf", "parseString", "parse", "fromString", "ofString", "from", "of");
    
    public GenericStaticCondition(T value) {
        super(value);
    }
    
    public GenericStaticCondition(Class<T> type, String value) throws NoStringParserAvailableException {
        this(getStringParser(type).apply(value));
    }
    
    private static <T> Function<String, T> getStringParser(Class<T> type) throws NoStringParserAvailableException {
        Method parser = SUPPORTED_PARSING_METHODS.stream()
                .map(name -> getOptionalMethod(type, name, String.class))
                .flatMap(Optional::stream)
                .filter(method -> method.canAccess(null))
                .filter(method -> type.isAssignableFrom(method.getReturnType()))
                .findFirst()
                .orElseThrow(() -> new NoStringParserAvailableException("%s must implement one of the following public static methods: %s"
                        .formatted(type.getSimpleName(), SUPPORTED_PARSING_METHODS)));
 
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
