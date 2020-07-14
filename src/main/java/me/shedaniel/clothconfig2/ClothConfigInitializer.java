package me.shedaniel.clothconfig2;

import me.shedaniel.clothconfig2.api.ScrollingContainer;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.clothconfig2.impl.EasingMethod.EasingMethodImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ClothConfigInitializer {
    public static final Logger LOGGER = LogManager.getFormatterLogger("ClothConfig");
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double handleScrollingPosition(double[] target, double scroll, double maxScroll, float delta, double start, double duration) {
        return ScrollingContainer.handleScrollingPosition(target, scroll, maxScroll, delta, start, duration);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double expoEase(double start, double end, double amount) {
        return ScrollingContainer.ease(start, end, amount, getEasingMethod());
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double clamp(double v, double maxScroll) {
        return ScrollingContainer.clampExtension(v, maxScroll);
    }
    
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static double clamp(double v, double maxScroll, double clampExtension) {
        return ScrollingContainer.clampExtension(v, -clampExtension, maxScroll + clampExtension);
    }
    
    public static EasingMethod getEasingMethod() {
        return EasingMethodImpl.NONE;
    }
    
    public static long getScrollDuration() {
        return 0;
    }
    
    public static double getScrollStep() {
        return 16.0;
    }
    
    public static double getBounceBackMultiplier() {
        return -10;
    }
    
    static {
        printClassPath();
    }
    
    public static void printClassPath() {
        System.out.println(
                Arrays.stream(
                        ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs()
                ).map(
                        url ->  url.getFile().replace("%20", " ") 
                ).collect(Collectors.joining("\n"))
        );
    }
}
