package me.shedaniel.clothconfig2.forge.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EasingMethods {
    private static final List<EasingMethod> METHODS;
    
    static {
        METHODS = new ArrayList<>();
        METHODS.addAll(Arrays.asList(EasingMethod.EasingMethodImpl.values()));
    }
    
    public static void register(EasingMethod easingMethod) {
        METHODS.add(easingMethod);
    }
    
    public static List<EasingMethod> getMethods() {
        return Collections.unmodifiableList(METHODS);
    }
}
