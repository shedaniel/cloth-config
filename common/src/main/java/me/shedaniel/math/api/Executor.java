package me.shedaniel.math.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Executor {
    
    private Executor() {}
    
    public static void run(Supplier<Runnable> runnableSupplier) {
        runnableSupplier.get().run();
    }
    
    public static void runIf(Supplier<Boolean> predicate, Supplier<Runnable> runnableSupplier) {
        if (predicate.get())
            runnableSupplier.get().run();
    }
    
    public static void runIfEnv(EnvType env, Supplier<Runnable> runnableSupplier) {
        if (FabricLoader.getInstance().getEnvironmentType() == env)
            runnableSupplier.get().run();
    }
    
    public static <T> T call(Supplier<Callable<T>> runnableSupplier) {
        try {
            return runnableSupplier.get().call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> Optional<T> callIf(Supplier<Boolean> predicate, Supplier<Callable<T>> runnableSupplier) {
        if (predicate.get())
            try {
                return Optional.of(runnableSupplier.get().call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return Optional.empty();
    }
    
    public static <T> Optional<T> callIfEnv(EnvType env, Supplier<Callable<T>> runnableSupplier) {
        if (FabricLoader.getInstance().getEnvironmentType() == env)
            try {
                return Optional.of(runnableSupplier.get().call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return Optional.empty();
    }
    
}
