/*
 * This file is part of Cloth Config.
 * Copyright (C) 2020 - 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
