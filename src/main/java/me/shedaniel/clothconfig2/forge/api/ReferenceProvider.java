package me.shedaniel.clothconfig2.forge.api;

import org.jetbrains.annotations.NotNull;

public interface ReferenceProvider<T> {
    @NotNull
    AbstractConfigEntry<T> provideReferenceEntry();
}
