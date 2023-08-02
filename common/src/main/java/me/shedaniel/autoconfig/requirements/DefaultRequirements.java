package me.shedaniel.autoconfig.requirements;

import java.util.Objects;

public class DefaultRequirements {
    private DefaultRequirements() {}
    
    /**
     * The condition is met when the argument is {@code true}.
     * 
     * @see #isTrue(Boolean) 
     */
    public static final String TRUE = "isTrue";
    
    /**
     * The condition is met when the argument is {@code false}.
     * 
     * @see #isFalse(Boolean) 
     */
    public static final String FALSE = "isFalse";
    
    /**
     * The condition is met when the first argument is equal to the second.
     * 
     * @see #is(Object, Object) 
     */
    public static final String IS = "is";
    
    /**
     * The condition is met when at least one {@code arg} is equal to another.
     * 
     * @see #anyMatch(Object[]) 
     */
    public static final String ANY_MATCH = "anyMatch";
    
    private static boolean isTrue(Boolean value) {
        return Boolean.TRUE.equals(value);
    }
    
    private static boolean isFalse(Boolean value) {
        return Boolean.FALSE.equals(value);
    }
    
    private static <T> boolean is(T value, T condition) {
        return Objects.equals(value, condition);
    }
    
    private static <T> boolean anyMatch(T ...args) {
        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < i; j++) {
                if (Objects.equals(args[i], args[j])) {
                    return true;
                }
            }
            for (int j = i+1; j < args.length; j++) {
                if (Objects.equals(args[i], args[j])) {
                    return true;
                }
            }
        }
        return false;
    }
}
